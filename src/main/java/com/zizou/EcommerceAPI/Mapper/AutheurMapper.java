package com.zizou.EcommerceAPI.Mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.zizou.EcommerceAPI.Dto.AutheurDto;
import com.zizou.EcommerceAPI.Entity.Autheur;

@Mapper(componentModel = "spring", uses = {LivreMapper.class})
public interface AutheurMapper {

    AutheurDto toDto(Autheur autheur);

    List<AutheurDto> toDtoList(List<Autheur> autheurs);
}
