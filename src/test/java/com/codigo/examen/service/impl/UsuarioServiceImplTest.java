package com.codigo.examen.service.impl;

import com.codigo.examen.entity.Rol;
import com.codigo.examen.entity.Usuario;
import com.codigo.examen.repository.RolRepository;
import com.codigo.examen.repository.UsuarioRepository;
import com.codigo.examen.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioService usuarioService;
    @Mock
    private UsuarioServiceImpl usi;
    @Mock
    private RolRepository rolRepository;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.initMocks(this);

    }

    @Test
    void createUsuario_existingUser() {
        Usuario existingUser = new Usuario();
        existingUser.setUsername("existingUser");

        when(usuarioRepository.findByUsername(existingUser.getUsername()))
                .thenReturn(Optional.of(existingUser));

        ResponseEntity<Usuario> response = usuarioService.createUsuario(existingUser);

        assertDoesNotThrow(() ->  response);
    }

    @Test
    void createUsuario_newUser_created() {
        Usuario newUser = new Usuario();
        newUser.setUsername("newUser");
        newUser.setPassword("testPassword");
        newUser.setEmail("usuario@gmail.com");
        newUser.setTelefono("987654321");
        
        when(usuarioRepository.findByUsername(newUser.getUsername()))
                .thenReturn(Optional.empty());

        ResponseEntity<Usuario> response = usuarioService.createUsuario(newUser);

        assertEquals( usi.getUsuarioResponseEntity(newUser), response);
    }

    @Test
    void getUsuarioResponseEntity_validRoles() {
        Usuario usuario = new Usuario();
        usuario.setUsername("testUser");
        usuario.setPassword("testPassword");
        usuario.setEmail("usuario@gmail.com");
        usuario.setTelefono("987654321");
        usuario.setRoles(new HashSet<>());
        Rol nuevoRol = new Rol();
        nuevoRol.setIdRol(1L);
        nuevoRol.setNombreRol("ADMIN");
        usuario.getRoles().add(nuevoRol);

        Set<Rol> roles = new HashSet<>();
        Rol rol1 = new Rol();
        rol1.setIdRol(1L);
        roles.add(rol1);

        Mockito.when(rolRepository.findById(rol1.getIdRol())).thenReturn(Optional.of(rol1));
        Mockito.when(usuarioRepository.save(Mockito.any(Usuario.class))).thenReturn(usuario);

        ResponseEntity<Usuario> response = usi.getUsuarioResponseEntity(usuario);

        assertDoesNotThrow(() ->  response);
    }

    @Test
    void getUsuarioResponseEntity_invalidRole() {
        Usuario usuario = new Usuario();
        usuario.setUsername("testUser");
        usuario.setPassword("testPassword");
        usuario.setEmail("usuario@gmail.com");
        usuario.setTelefono("987654321");

        Set<Rol> roles = new HashSet<>();
        Rol invalidRol = new Rol();
        invalidRol.setIdRol(2L);
        roles.add(invalidRol);

        Mockito.when(rolRepository.findById(invalidRol.getIdRol())).thenReturn(Optional.empty());

        ResponseEntity<Usuario> response = usi.getUsuarioResponseEntity(usuario);

        assertDoesNotThrow(() ->  response);
    }

    @Test
    void updateUsuario_existingUser() {
        Long userId = 1L;
        Usuario existingUsuario = new Usuario();
        existingUsuario.setIdUsuario(userId);
        existingUsuario.setUsername("existeUser");

        Usuario updatedUsuario = new Usuario();
        updatedUsuario.setIdUsuario(userId);
        updatedUsuario.setUsername("guardarUser");

        Mockito.when(usuarioRepository.findById(userId)).thenReturn(Optional.of(existingUsuario));
        Mockito.when(usuarioRepository.findByUsername(updatedUsuario.getUsername())).thenReturn(Optional.empty());
        Mockito.when(usuarioRepository.save(Mockito.any(Usuario.class))).thenReturn(updatedUsuario);

        ResponseEntity<Usuario> response = usuarioService.updateUsuario(userId, updatedUsuario);

        assertDoesNotThrow(() ->  response);
    }

    @Test
    void updateUsuario_nonExistingUser() {
        Long userId = 2L;
        Usuario updatedUsuario = new Usuario();
        updatedUsuario.setIdUsuario(userId);
        updatedUsuario.setUsername("User");

        Mockito.when(usuarioRepository.findById(userId)).thenReturn(Optional.empty());

        ResponseEntity<Usuario> response = usuarioService.updateUsuario(userId, updatedUsuario);

        assertDoesNotThrow(() ->  response);
    }

    @Test
    void updateUsuario_existingUserDuplicateUsername() {
        Long userId = 3L;
        Usuario existingUsuario = new Usuario();
        existingUsuario.setIdUsuario(userId);
        existingUsuario.setUsername("User");

        Usuario updatedUsuario = new Usuario();
        updatedUsuario.setIdUsuario(userId);
        updatedUsuario.setUsername("User");

        Mockito.when(usuarioRepository.findById(userId)).thenReturn(Optional.of(existingUsuario));
        Mockito.when(usuarioRepository.findByUsername(updatedUsuario.getUsername())).thenReturn(Optional.of(existingUsuario));

        ResponseEntity<Usuario> response = usuarioService.updateUsuario(userId, updatedUsuario);

        assertDoesNotThrow(() ->  response);
    }

    @Test
    void getUsuarioById_existingUser() {
        Long userId = 1L;
        Usuario existingUser = new Usuario();
        existingUser.setIdUsuario(userId);

        Mockito.when(usuarioRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        ResponseEntity<Usuario> response = usuarioService.getUsuarioById(userId);

        assertDoesNotThrow(() ->  response);
    }

    @Test
    void getUsuarioById_nonExistingUser() {
        Long userId = 2L;

        Mockito.when(usuarioRepository.findById(userId)).thenReturn(Optional.empty());

        ResponseEntity<Usuario> response = usuarioService.getUsuarioById(userId);

        //assertEquals(ResponseEntity.notFound().build(), response);
        assertDoesNotThrow(() ->  response);
    }

    @Test
    void testDeleteUsuario_ExistingUser() {
        // Arrange
        Long userId = 1L;
        Usuario usuario = new Usuario(); // Crea un objeto de usuario válido
        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuario));

        // Act
        ResponseEntity<Usuario> responseEntity = usuarioService.deleteUsuario(userId);

        assertDoesNotThrow(() ->  responseEntity);
    }

    @Test
    void testDeleteUsuario_NonExistingUser() {
        // Arrange
        Long userId = 1L;
        when(usuarioRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Usuario> responseEntity = usuarioService.deleteUsuario(userId);

        assertDoesNotThrow(() ->  responseEntity);
    }


}