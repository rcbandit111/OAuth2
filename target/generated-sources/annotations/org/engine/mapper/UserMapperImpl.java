package org.engine.mapper;

import java.time.format.DateTimeFormatter;
import javax.annotation.processing.Generated;
import org.engine.production.entity.Users;
import org.engine.production.entity.Users.UsersBuilder;
import org.engine.rest.dto.UserDTO;
import org.engine.rest.dto.UserDTO.UserDTOBuilder;
import org.engine.rest.dto.UserFilter;
import org.engine.rest.dto.UserFilterDTO;
import org.engine.rest.dto.UserNewDTO;
import org.engine.rest.dto.UserNewDTO.UserNewDTOBuilder;
import org.engine.utils.Role;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-07-16T01:44:06+0300",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 14 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDTO toDTO(Users user) {
        if ( user == null ) {
            return null;
        }

        UserDTOBuilder userDTO = UserDTO.builder();

        userDTO.id( user.getId() );
        userDTO.login( user.getLogin() );
        userDTO.firstName( user.getFirstName() );
        userDTO.lastName( user.getLastName() );
        userDTO.email( user.getEmail() );
        if ( user.getRole() != null ) {
            userDTO.role( user.getRole().name() );
        }
        userDTO.enabled( user.getEnabled() );
        if ( user.getCreatedAt() != null ) {
            userDTO.createdAt( DateTimeFormatter.ISO_LOCAL_DATE_TIME.format( user.getCreatedAt() ) );
        }

        return userDTO.build();
    }

    @Override
    public UserNewDTO toNewDTO(Users user) {
        if ( user == null ) {
            return null;
        }

        UserNewDTOBuilder userNewDTO = UserNewDTO.builder();

        userNewDTO.id( user.getId() );
        userNewDTO.login( user.getLogin() );
        userNewDTO.firstName( user.getFirstName() );
        userNewDTO.lastName( user.getLastName() );
        userNewDTO.email( user.getEmail() );
        if ( user.getRole() != null ) {
            userNewDTO.role( user.getRole().name() );
        }
        userNewDTO.enabled( user.getEnabled() );
        userNewDTO.ownerId( user.getOwnerId() );
        userNewDTO.ownerType( user.getOwnerType() );
        userNewDTO.createdAt( user.getCreatedAt() );

        return userNewDTO.build();
    }

    @Override
    public Users map(UserNewDTO userDTO) {
        if ( userDTO == null ) {
            return null;
        }

        UsersBuilder users = Users.builder();

        if ( userDTO.getId() != null ) {
            users.id( userDTO.getId() );
        }
        users.login( userDTO.getLogin() );
        users.email( userDTO.getEmail() );
        users.enabled( userDTO.getEnabled() );
        users.ownerId( userDTO.getOwnerId() );
        users.ownerType( userDTO.getOwnerType() );
        if ( userDTO.getRole() != null ) {
            users.role( Enum.valueOf( Role.class, userDTO.getRole() ) );
        }
        users.firstName( userDTO.getFirstName() );
        users.lastName( userDTO.getLastName() );
        users.createdAt( userDTO.getCreatedAt() );

        return users.build();
    }

    @Override
    public UserFilter toFilter(UserFilterDTO dto) {
        if ( dto == null ) {
            return null;
        }

        UserFilter userFilter = new UserFilter();

        userFilter.setId( dto.getId() );
        userFilter.setName( dto.getName() );
        userFilter.setFrom( dto.getFrom() );
        userFilter.setTo( dto.getTo() );

        return userFilter;
    }
}
