package br.com.fiap.autoescola.mapper;

import br.com.fiap.autoescola.dto.UsuarioCreateDTO;
import br.com.fiap.autoescola.entity.Usuario;
import br.com.fiap.autoescola.vo.UsuarioVO;

public class UsuarioMapper {
    public static Usuario toEntity(UsuarioCreateDTO dto, String senhaCriptografada) {
        return Usuario.builder()
                .username(dto.getUsername())
                .password(senhaCriptografada)
                .role(dto.getRole())
                .build();
    }

    public static UsuarioVO toVO(Usuario entity) {
        return UsuarioVO.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .role(entity.getRole())
                .build();
    }
}