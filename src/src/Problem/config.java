/**
 * 
 */
package src.Problem;

import java.util.HashMap;

/**
 * @author Berit Larsen
 *
 */
public class config {

    @SuppressWarnings("rawtypes")
    private HashMap config = new HashMap();

    @SuppressWarnings("unchecked")
    public void addParam(String param, String value) {
	config.put(param, value);
    }

    public String getParam(String param) {
	return (String) config.get(param);
    }

    public void newSession() {
	config = new HashMap();
    }
}
