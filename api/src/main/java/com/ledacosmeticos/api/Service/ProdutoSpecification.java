package com.ledacosmeticos.api.Service; // Ou onde fizer mais sentido no seu projeto

import com.ledacosmeticos.api.Model.Produto;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ProdutoSpecification {

    public Specification<Produto> findWithFilters(String nome, UUID categoriaId, Boolean ativo) {
        return (root, query, criteriaBuilder) -> {
            
            // Criamos uma lista para guardar todas as nossas "condições" (os 'WHERE' da query)
            List<Predicate> predicates = new ArrayList<>();

            // 1. Se um 'nome' foi fornecido, adicionamos a condição de pesquisa por nome
            if (nome != null && !nome.isEmpty()) {
                // Esta é a forma programática e segura de fazer: lower(p.nome) LIKE '%nome%'
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nome")), "%" + nome.toLowerCase() + "%"));
            }

            // 2. Se um 'categoriaId' foi fornecido, adicionamos a condição de filtro por categoria
            if (categoriaId != null) {
                predicates.add(criteriaBuilder.equal(root.get("categoria").get("id"), categoriaId));
            }

            // 3. Se o status 'ativo' foi fornecido, adicionamos a condição
            if (ativo != null) {
                predicates.add(criteriaBuilder.equal(root.get("ativo"), ativo));
            }

            // Combinamos todas as condições com "AND" e retornamos a query final
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}