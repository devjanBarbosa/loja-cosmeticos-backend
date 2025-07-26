package com.ledacosmeticos.api.Service; // Verifique se o seu pacote está correto

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ledacosmeticos.api.Model.Categoria;
import com.ledacosmeticos.api.Repository.CategoriaRepository;

@Service
public class CategoryService {

    // Injeta o "gerente de estoque" das categorias
    @Autowired
    private CategoriaRepository categoriaRepository;

    // Método para buscar todas as categorias
    public List<Categoria> listarTodas() {
        // Delega a tarefa para o repositório
        return categoriaRepository.findAll();
    }
}