package com.zizou.EcommerceAPI.Mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.zizou.EcommerceAPI.Dto.CommandDto;
import com.zizou.EcommerceAPI.Entity.Command;

@Mapper(componentModel = "spring", uses = {CommandItemMapper.class})
public interface CommandMapper {

    @Mapping(source = "user.id",  target = "userId")
    @Mapping(source = "user.nom", target = "userNom")
    CommandDto toDto(Command command);

    List<CommandDto> toDtoList(List<Command> commands);
}
