/**
 * Bu sınıf, uygulama çalıştığı sürede oturum açan kullanıcıların bilgilerini tutar.
 * 
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   20.07.2016 17:01:16
 */
package com.mepsan.marwiz.general.pattern;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;


//@ManagedBean(name="sessionManager")
//@ApplicationScoped
public class HttpSessionCollector implements HttpSessionListener {
    private static final Map<String, HttpSession> sessions = new HashMap<String, HttpSession>();

    @Override
    public void sessionCreated(HttpSessionEvent event) {  
        HttpSession session = event.getSession();
        sessions.put(session.getId(), session);
    }


    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        sessions.remove(event.getSession().getId());
    }

    public static HttpSession find(String sessionId) {
        return sessions.get(sessionId);
    }

  

}