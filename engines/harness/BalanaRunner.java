import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.wso2.balana.*;
import org.wso2.balana.ctx.*;
import org.wso2.balana.finder.*;

/**
 * Batch runner: each input line is "<id>\t<policyFile>\t<requestFile>".
 * Output line: "<id>\t<Decision>" where Decision is P | D | NA | IND | ERR.
 *
 * The single root policy is exposed through a PolicyFinderModule whose findPolicy()
 * logic mirrors Balana's own FileBasedPolicyFinderModule (target match at the top level),
 * so this is the standard Balana deployment path.
 */
public class BalanaRunner {

    static class SingleModule extends PolicyFinderModule {
        AbstractPolicy root;
        Map<URI, AbstractPolicy> refs = new HashMap<>();
        PolicyFinder finder;
        String rootPath;
        SingleModule(String rootPath) { this.rootPath = rootPath; }
        @Override public void init(PolicyFinder f) {
            finder = f;
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                dbf.setNamespaceAware(true);
                DocumentBuilder db = dbf.newDocumentBuilder();
                Element el = db.parse(new File(rootPath)).getDocumentElement();
                String name = DOMHelper.getLocalName(el);
                root = name.equals("Policy") ? Policy.getInstance(el) : PolicySet.getInstance(el, finder);
            } catch (Exception e) { throw new RuntimeException("load: " + e, e); }
        }
        @Override public boolean isRequestSupported() { return true; }
        @Override public boolean isIdReferenceSupported() { return true; }
        @Override public PolicyFinderResult findPolicy(EvaluationCtx context) {
            MatchResult match = root.match(context);
            int r = match.getResult();
            if (r == MatchResult.INDETERMINATE) return new PolicyFinderResult(match.getStatus());
            if (r == MatchResult.MATCH) return new PolicyFinderResult(root);
            return new PolicyFinderResult();
        }
        @Override public PolicyFinderResult findPolicy(URI id, int type, VersionConstraints c, PolicyMetaData m) {
            AbstractPolicy p = refs.get(id);
            if (p != null) return new PolicyFinderResult(p);
            ArrayList<String> code = new ArrayList<>();
            code.add(Status.STATUS_PROCESSING_ERROR);
            return new PolicyFinderResult(new Status(code, "couldn't load referenced policy"));
        }
    }

    public static void main(String[] args) throws Exception {
        Balana balana = Balana.getInstance();
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "UTF-8"));
        PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(args[1]), "UTF-8"));
        String line;
        while ((line = in.readLine()) != null) {
            if (line.isBlank()) continue;
            String[] f = line.split("\t");
            String dec;
            try {
                PolicyFinder pf = new PolicyFinder();
                Set<PolicyFinderModule> mods = new HashSet<>();
                mods.add(new SingleModule(f[1]));
                pf.setModules(mods);
                pf.init();
                PDPConfig base = balana.getPdpConfig();
                PDP pdp = new PDP(new PDPConfig(base.getAttributeFinder(), pf, base.getResourceFinder(), false));
                String req = new String(Files.readAllBytes(Paths.get(f[2])), "UTF-8");
                String resp = pdp.evaluate(req);
                if (resp.contains("<Decision>Permit</Decision>")) dec = "P";
                else if (resp.contains("<Decision>Deny</Decision>")) dec = "D";
                else if (resp.contains("<Decision>NotApplicable</Decision>")) dec = "NA";
                else if (resp.contains("<Decision>Indeterminate</Decision>")) dec = "IND";
                else dec = "ERR";
            } catch (Throwable t) {
                dec = "ERR";
                System.err.println(f[0] + " " + t);
            }
            out.println(f[0] + "\t" + dec);
        }
        out.close();
    }
}
