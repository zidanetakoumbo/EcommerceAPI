package com.zizou.EcommerceAPI.Mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.zizou.EcommerceAPI.Dto.CategorieDto;
import com.zizou.EcommerceAPI.Entity.Categorie;

@Mapper(componentModel = "spring", uses = {LivreMapper.class})
public interface CategorieMapper {

    CategorieDto toDto(Categorie categorie);

    List<CategorieDto> toDtoList(List<Categorie> categories);
}
