package com.ledacosmeticos.api.Service; // ou br.com...

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.ledacosmeticos.api.Repository.LojistaRepository;

@Service
public class AutenticacaoService implements UserDetailsService {

    @Autowired
    private LojistaRepository lojistaRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return lojistaRepository.findByEmail(username);
    }
}