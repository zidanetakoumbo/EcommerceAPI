package com.zizou.EcommerceAPI.Mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.zizou.EcommerceAPI.Dto.AppUserDto;
import com.zizou.EcommerceAPI.Entity.AppUser;

@Mapper(componentModel = "spring")
public interface AppUserMapper {

    AppUserDto toDto(AppUser user);

    List<AppUserDto> toDtoList(List<AppUser> users);
}
