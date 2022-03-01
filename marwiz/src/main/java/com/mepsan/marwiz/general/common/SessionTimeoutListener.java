/**
 * Bu Sınıf ...
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   09.01.2018 16:26:37
 */
package com.mepsan.marwiz.general.common;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import javax.faces.component.UIViewRoot;

public class SessionTimeoutListener implements PhaseListener {

    private String getLoginPath() {
        return "/pages/login.xhtml";
    }

    @Override
    public void beforePhase(final PhaseEvent event) {
        final FacesContext facesContext = FacesContext.getCurrentInstance();
        if (!facesContext.getPartialViewContext().isAjaxRequest() || facesContext.getRenderResponse()) {
           // not ajax or too late
            return;
        }

        final HttpServletRequest request = HttpServletRequest.class.cast(facesContext.getExternalContext().getRequest());
        if (request.getDispatcherType() == DispatcherType.FORWARD && getLoginPath().equals(request.getServletPath())) { // isLoginRedirection()
            final String redirect = facesContext.getExternalContext().getRequestContextPath() ;//+ request.getServletPath();
            try {
                Map<String, String> reqParams = event.getFacesContext().getExternalContext().getRequestParameterMap();
                boolean login = false;
                for (String key : reqParams.keySet()) {//Find where is request (If login dont do anything!)
                    if (("loginForm").equals(key)) {
                        login = true;
                        break;
                    }
                }
                if (!login) {
                    facesContext.getExternalContext().redirect(redirect);
                    System.out.println(" session timeout ");
                }
            } catch (Exception e) {
            }

            //System.out.println(component.getClientId()+"--- "+request.getRemoteUser());
//            try {
//                System.out.println("ajax");
//              //  facesContext.getExternalContext().redirect(redirect);
//            } catch (final IOException e) {
//                // here use you preferred logging framework to log this error
//            }
        }
    }

    @Override
    public void afterPhase(final PhaseEvent event) {
        //  printLog(event, "after phase:"); 
        // no-op
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }

    protected void printLog(PhaseEvent event, String msg) {
        UIViewRoot view = event.getFacesContext().getViewRoot();
        String viewID = "no view";
        if (view != null) {
            viewID = view.getViewId();
        }
        System.out.println(msg + event.getPhaseId() + " " + viewID);

        printRequestParameters(event);
        printRequestAttributes(event);
        printSessionAttributes(event);

    }

    private void printSessionAttributes(PhaseEvent event) {
        Map<String, Object> sessAttrs = event.getFacesContext().getExternalContext().getSessionMap();
        StringBuilder sb = new StringBuilder();
        for (String key : sessAttrs.keySet()) {
            sb.append("(" + key + "=" + sessAttrs.get(key) + ") ");
        }
        System.out.println("Session Attributes : " + sb.toString());
    }

    private void printRequestAttributes(PhaseEvent event) {
        Map<String, Object> reqAttrs = event.getFacesContext().getExternalContext().getRequestMap();
        StringBuilder sb = new StringBuilder();
        for (String key : reqAttrs.keySet()) {
            sb.append("(" + key + "=" + reqAttrs.get(key) + ") ");
        }
        System.out.println("Request Attributes : " + sb.toString());
    }

    private void printRequestParameters(PhaseEvent event) {
        Map<String, String> reqParams = event.getFacesContext().getExternalContext().getRequestParameterMap();
        StringBuilder sb = new StringBuilder();
        for (String key : reqParams.keySet()) {
            sb.append("(" + key + "=" + reqParams.get(key) + ") ");
        }
        System.out.println("Request Parameters : " + sb.toString());
    }
}
