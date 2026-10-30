import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.DecisionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.Request;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.Response;
import org.ow2.authzforce.core.pdp.api.XmlUtils.XmlnsFilteringParser;
import org.ow2.authzforce.core.pdp.api.XmlUtils.XmlnsFilteringParserFactory;
import org.ow2.authzforce.core.pdp.api.io.PdpEngineInoutAdapter;
import org.ow2.authzforce.core.pdp.api.io.XacmlJaxbParsingUtils;
import org.ow2.authzforce.core.pdp.impl.PdpEngineConfiguration;
import org.ow2.authzforce.core.pdp.impl.io.PdpEngineAdapters;

/**
 * Batch runner for AuthzForce CE (same interface as BalanaRunner and OpenAZRunner).
 * Input line: "<id>\t<policyFile>\t<requestFile>"; output line: "<id>\t<P|D|NA|IND|ERR>".
 * Each policy is the single policy of a StaticPolicyProvider, hence the root policy, configured
 * exactly as in AuthzForce's own conformance tests (pdp-cli/src/test/resources/conformance).
 */
public class AuthzForceRunner {
    private static final String CONF =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        + "<pdp xmlns=\"http://authzforce.github.io/core/xmlns/pdp/8\" "
        + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"8.0\">\n"
        + "  <policyProvider id=\"rootPolicyProvider\" xsi:type=\"StaticPolicyProvider\">\n"
        + "    <policyLocation>%s</policyLocation>\n"
        + "  </policyProvider>\n"
        + "  <ioProcChain>\n"
        + "    <requestPreproc>urn:ow2:authzforce:feature:pdp:request-preproc:xacml-xml:default-lax</requestPreproc>\n"
        + "  </ioProcChain>\n"
        + "</pdp>\n";

    private static String xmlEscape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    public static void main(String[] args) throws Exception {
        Path conf = Files.createTempFile("authzforce-pdp", ".xml");
        conf.toFile().deleteOnExit();
        XmlnsFilteringParserFactory parsers = XacmlJaxbParsingUtils.getXacmlParserFactory(false);
        try (BufferedReader in = Files.newBufferedReader(Paths.get(args[0]), StandardCharsets.UTF_8);
             PrintWriter out = new PrintWriter(Files.newBufferedWriter(Paths.get(args[1]), StandardCharsets.UTF_8))) {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] f = line.split("\t");
                String dec;
                try {
                    String loc = new File(f[1]).getAbsoluteFile().toURI().toString();
                    Files.write(conf, String.format(CONF, xmlEscape(loc)).getBytes(StandardCharsets.UTF_8));
                    PdpEngineConfiguration cfg = PdpEngineConfiguration.getInstance(conf.toString());
                    XmlnsFilteringParser parser = parsers.getInstance();
                    Object req = parser.parse(new File(f[2]).toURI().toURL());
                    if (!(req instanceof Request)) throw new IllegalArgumentException("not a XACML 3.0 Request");
                    try (PdpEngineInoutAdapter<Request, Response> pdp = PdpEngineAdapters.newXacmlJaxbInoutAdapter(cfg)) {
                        Response resp = pdp.evaluate((Request) req, parser.getNamespacePrefixUriMap());
                        DecisionType d = resp.getResults().get(0).getDecision();
                        switch (d) {
                            case PERMIT: dec = "P"; break;
                            case DENY: dec = "D"; break;
                            case NOT_APPLICABLE: dec = "NA"; break;
                            default: dec = "IND";
                        }
                    }
                } catch (Throwable t) {
                    dec = "ERR";
                    System.err.println(f[0] + " " + t);
                }
                out.println(f[0] + "\t" + dec);
            }
        }
    }
}
