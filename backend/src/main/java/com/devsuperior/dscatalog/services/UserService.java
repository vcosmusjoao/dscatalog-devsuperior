package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.*;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Role;
import com.devsuperior.dscatalog.entities.User;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.RoleRepository;
import com.devsuperior.dscatalog.repositories.UserRepository;
import com.devsuperior.dscatalog.services.excepetion.DatabaseException;
import com.devsuperior.dscatalog.services.excepetion.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
public class UserService implements UserDetailsService {

    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;


    //LISTAR TODOS OS PRODUTOS
    @Transactional(readOnly = true)
    public Page<UserDTO> findAllPaged(Pageable pageable){
        Page<User> page =  userRepository.findAll(pageable);
        Page<UserDTO> pageDTO = page
                .map(x -> new UserDTO(x));
        return pageDTO;
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id){
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ID Não Encontrado"));

        UserDTO UserDTO = new UserDTO(user);
        return UserDTO;

    }

    @Transactional
    public UserDTO add (UserInsertDTO dto){
        User userEntity = new User();
        copyDTOtoEntity(dto,userEntity);
        userEntity.setPassword(passwordEncoder.encode(dto.getPassword()));
        userEntity= userRepository.save(userEntity);
        return new UserDTO(userEntity);
    }

    @Transactional
    public UserDTO update(Long id, UserUpdateDTO userDTO) {
        try{
            User userEntity = userRepository.getOne(id);
            copyDTOtoEntity(userDTO,userEntity);
            userEntity = userRepository.save(userEntity);
            return new UserDTO(userEntity);
        }catch(EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id Não Encontrado "+id);
        }

    }

    public void delete(Long id) {
        try{
            userRepository.deleteById(id);
        }catch(EmptyResultDataAccessException e){
            throw new ResourceNotFoundException(("Id não encontrado ")+ id);
        }catch(DataIntegrityViolationException e){//caso eu apague uma categoria que iria comprometer a integridade do banco, por exemplo, eu apagar uma categoria que tem varios produtos dependendo dessa categoria
            throw new DatabaseException("Violação de Integridade");
        }
    }

    private void copyDTOtoEntity(UserDTO dto, User entity){
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
        entity.getRoles().clear();
        for(RoleDTO roleDTO :dto.getRoles()){
            Role role = roleRepository.getOne(roleDTO.getId());
            entity.getRoles().add(role);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if( user == null){
            logger.error("User not found: "+email);
            throw  new UsernameNotFoundException("Email not found");
        }
        logger.info("User found: "+ email);
        return user;
    }
}
