package com.ledacosmeticos.api.Service;

import com.ledacosmeticos.api.Model.Categoria;
import com.ledacosmeticos.api.Model.TipoCategoria; // 1. Importe
import com.ledacosmeticos.api.Repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    // --- MÉTODO ATUALIZADO ---
    public List<Categoria> listarTodas(TipoCategoria tipo) {
        // Se um tipo for fornecido, filtra por ele
        if (tipo != null) {
            return categoriaRepository.findByTipo(tipo);
        }
        // Se não, retorna todas as categorias
        return categoriaRepository.findAll();
    }

      public Categoria criar(Categoria categoria) {
        // Futuramente, podemos adicionar validações aqui
        return categoriaRepository.save(categoria);
    }

    public Categoria atualizar(UUID id, Categoria categoriaDados) {
        Categoria categoriaExistente = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada com ID: " + id));
        
        categoriaExistente.setNome(categoriaDados.getNome());
        categoriaExistente.setTipo(categoriaDados.getTipo()); // Permite alterar o tipo também
        
        return categoriaRepository.save(categoriaExistente);
    }

    public void deletar(UUID id) {
        if (!categoriaRepository.existsById(id)) {
            throw new RuntimeException("Categoria não encontrada com ID: " + id);
        }
        // Adicionar verificação se a categoria está em uso por algum produto antes de apagar
        categoriaRepository.deleteById(id);
    }
    
}