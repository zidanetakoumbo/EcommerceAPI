package com.zizou.EcommerceAPI.Mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.zizou.EcommerceAPI.Dto.CommandItemDto;
import com.zizou.EcommerceAPI.Entity.CommandItem;

@Mapper(componentModel = "spring")
public interface CommandItemMapper {

    @Mapping(source = "livre.id",     target = "livreId")
    @Mapping(source = "livre.titre",  target = "titreLivre")
    @Mapping(target = "prixTotal",    expression = "java(item.getPrixTotal())")
    CommandItemDto toDto(CommandItem item);

    List<CommandItemDto> toDtoList(List<CommandItem> items);
}
