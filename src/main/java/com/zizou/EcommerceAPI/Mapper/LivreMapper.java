package com.zizou.EcommerceAPI.Mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.zizou.EcommerceAPI.Dto.LivreDto;
import com.zizou.EcommerceAPI.Entity.Categorie;
import com.zizou.EcommerceAPI.Entity.Livre;

@Mapper(componentModel = "spring")
public interface LivreMapper {

    @Mapping(source = "autheur.nom",    target = "autheurNom")
    @Mapping(source = "autheur.prenom", target = "autheurPrenom")
    @Mapping(target = "categoriesNoms", expression = "java(categoriesToNoms(livre.getCategories()))")
    LivreDto toDto(Livre livre);

    List<LivreDto> toDtoList(List<Livre> livres);

    default List<String> categoriesToNoms(List<Categorie> categories) {
        if (categories == null) return new ArrayList<>();
        return categories.stream()
                .map(Categorie::getNomCat)
                .collect(Collectors.toList());
    }
}
