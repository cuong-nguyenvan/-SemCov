import java.io.*;
import java.util.*;
import org.apache.openaz.xacml.api.*;
import org.apache.openaz.xacml.api.pdp.*;
import org.apache.openaz.xacml.std.dom.DOMRequest;

/**
 * Batch runner for Apache OpenAZ (the AT&T XACML 3.0 engine).
 * Input line: "<id>\t<policyFile>\t<requestFile>"; output line: "<id>\t<P|D|NA|IND|ERR>".
 * The policy is configured as the single root policy (xacml.rootPolicies).
 */
public class OpenAZRunner {
    public static void main(String[] args) throws Exception {
        Properties base = new Properties();
        try (InputStream in = OpenAZRunner.class.getResourceAsStream("/xacml.properties")) {
            base.load(in);
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), "UTF-8"));
        PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(args[1]), "UTF-8"));
        PDPEngineFactory factory = PDPEngineFactory.newInstance(base);
        String line;
        while ((line = in.readLine()) != null) {
            if (line.isBlank()) continue;
            String[] f = line.split("\t");
            String dec;
            try {
                Properties p = new Properties();
                p.putAll(base);
                p.setProperty("xacml.rootPolicies", "root");
                p.setProperty("root.file", f[1]);
                p.setProperty("xacml.referencedPolicies", "");
                PDPEngine engine = factory.newEngine(p);
                Request req = DOMRequest.load(new File(f[2]));
                Response resp = engine.decide(req);
                Iterator<Result> it = resp.getResults().iterator();
                Decision d = it.next().getDecision();
                switch (d) {
                    case PERMIT: dec = "P"; break;
                    case DENY: dec = "D"; break;
                    case NOTAPPLICABLE: dec = "NA"; break;
                    default: dec = "IND";
                }
            } catch (Throwable t) {
                dec = "ERR";
                System.err.println(f[0] + " " + t);
            }
            out.println(f[0] + "\t" + dec);
        }
        out.close();
    }
}
