package com.zizou.EcommerceAPI.Mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.zizou.EcommerceAPI.Dto.PanierItemDto;
import com.zizou.EcommerceAPI.Entity.PanierItem;

@Mapper(componentModel = "spring")
public interface PanierItemMapper {

    @Mapping(source = "livre.id",             target = "livreId")
    @Mapping(source = "livre.titre",          target = "titreLivre")
    @Mapping(source = "livre.openCouverture", target = "openCouverture")
    @Mapping(source = "livre.prix",           target = "prixUnitaire")
    @Mapping(target = "prixTotal",            expression = "java(item.getPrixTotal())")
    PanierItemDto toDto(PanierItem item);

    List<PanierItemDto> toDtoList(List<PanierItem> items);
}
