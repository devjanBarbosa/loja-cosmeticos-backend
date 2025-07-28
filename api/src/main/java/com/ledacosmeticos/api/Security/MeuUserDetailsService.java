package com.ledacosmeticos.api.Security;

import java.util.Optional;

import com.ledacosmeticos.api.Model.Lojista;
import com.ledacosmeticos.api.Repository.LojistaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MeuUserDetailsService implements UserDetailsService {

  private final LojistaRepository lojistaRepository;

  @Autowired
  public MeuUserDetailsService(LojistaRepository lojistaRepository) {
    this.lojistaRepository = lojistaRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<Lojista> lojistaOpt = lojistaRepository.findByEmail(username);
    if (lojistaOpt.isEmpty()) {
      throw new UsernameNotFoundException("Lojista não encontrado: " + username);
    }
    return lojistaOpt.get();
  }
}
