package com.zizou.EcommerceAPI.Mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.zizou.EcommerceAPI.Dto.PanierDto;
import com.zizou.EcommerceAPI.Entity.Panier;

@Mapper(componentModel = "spring", uses = {PanierItemMapper.class})
public interface PanierMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(target = "totalPrice", expression = "java(panier.getTotalPrice())")
    PanierDto toDto(Panier panier);

    List<PanierDto> toDtoList(List<Panier> paniers);
}
