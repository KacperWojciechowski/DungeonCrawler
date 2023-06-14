package CommsFramework;

import org.json.JSONObject;

public interface SenderCallback {
    void send(JSONObject jsonObject);
}
