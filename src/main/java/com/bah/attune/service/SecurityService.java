package com.bah.attune.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.bah.attune.dao.MainDao;
import com.bah.attune.data.AttuneUser;

@Service
public class SecurityService implements UserDetailsService
{
    @Override
    public UserDetails loadUserByUsername(String username)
    {
        AttuneUser user = null;
        
        if ( username.equals("Attune"))
          user = new AttuneUser("Attune", "Attune");
        //user = new AttuneUser(username, "");

        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;

        return new User(user.getUsername(), user.getPassword(), enabled, accountNonExpired,
                        credentialsNonExpired, accountNonLocked, getAuthorities());

    }


    public Collection<GrantedAuthority> getAuthorities()
    {
        return getGrantedAuthorities(getRoles());
    }


    public List<String> getRoles()
    {
        List<String> roles = new ArrayList<String>();

        roles.add("ROLE_ATTUNEUSER");

        return roles;
    }


    public static List<GrantedAuthority> getGrantedAuthorities(List<String> roles)
    {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

        for (String role : roles)
            authorities.add(new SimpleGrantedAuthority(role));

        return authorities;
    }


    public static String getUserName()
    {
        String userName = null;

        if (SecurityContextHolder.getContext().getAuthentication() == null)
            return null;

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails)
        {
            userName = ((UserDetails) principal).getUsername();
        } else
        {
            userName = "Attune"; // for testing only
        }
        return userName;
    }
}
