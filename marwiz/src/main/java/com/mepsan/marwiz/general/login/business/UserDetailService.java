/**
 * Bu sınıf LoginService ve LoginDao arasında bağlantı sağlar.
 * Gerekli kontroller yapılır.
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   20.07.2016 17:01:16
 */
package com.mepsan.marwiz.general.login.business;

import com.mepsan.marwiz.general.model.general.UserData;
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailService implements UserDetailsService {

    private UserData user;

    public void setUser(UserData user) {
        this.user = user;
    }

    /**
     * Bu metot kullanıcı adını veritabanında sorgular
     *
     * @param username
     * @return user giriş yapan user nesnesi
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(final String username)
            throws UsernameNotFoundException {
        // user = findByUserName(username, password, userDataLogin);
        List<GrantedAuthority> authorities = buildUserAuthority("ROLE_ADMIN");
        if (user.getPassword().isEmpty()) {
            user.setPassword("");
        }
        return buildUserForAuthentication(user, authorities);
    }

    private User buildUserForAuthentication(UserData user,
            List<GrantedAuthority> authorities) {
        return new User(user.getUsername(),
                user.getPassword(), true,
                true, true, true, authorities);
    }

    private List<GrantedAuthority> buildUserAuthority(String userRoles) {
        Set<GrantedAuthority> setAuths = new HashSet<>();
        setAuths.add(new SimpleGrantedAuthority(userRoles));
        List<GrantedAuthority> Result = new ArrayList<>(setAuths);
        return Result;
    }

}
