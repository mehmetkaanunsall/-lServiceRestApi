/**
 * Bu Sınıf ...
 *
 *
 * @author Salem Walaa Abdulhadie 
 *
 * @date   01.11.2016 12:22:32 
 */

package com.mepsan.marwiz.general.common;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;


public class NoRedirectLogoutSuccessHandler implements LogoutSuccessHandler
{
    @Override
    public void onLogoutSuccess(HttpServletRequest request,
            HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException
    {
        // no redirect !! (unlike @SimpleUrlLogoutSuccessHandler, that redirects after logout)        
    }
}