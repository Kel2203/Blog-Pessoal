package com.example.blogpessoal.controller;

import com.example.blogpessoal.model.Usuario;
import com.example.blogpessoal.repository.UsuarioRepository;
import com.example.blogpessoal.service.UsuarioService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeAll
    void start() {
    usuarioRepository.deleteAll();
    usuarioService.cadastrarUsuario(new Usuario(0L, "Root", "root@email.com", "root123", " "));
    }

    @Test
    @DisplayName("👍 Cadastrar um usuário!")
    public void deveCriarUmUsuario() {
        HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L, "João Bastos", "joão_bastos@gmail.com", "123456789", "-"));

        ResponseEntity<Usuario> corpoResposta = testRestTemplate
                .exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);

        assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
    }

    @Test
    @DisplayName("😳 não deve permitir duplicação de usuários!")
    public void naoDeveDuplicarUsuario() {
        usuarioService.cadastrarUsuario(new Usuario(0L, "Maria da Silva", "maria@email.com", "12345678", "-"));
        HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L, "Maria da Silva", "maria@email.com", "12345678", "-"));
        ResponseEntity<Usuario> corpoResposta = testRestTemplate
                .exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
        assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());

    }

    @Test
    @DisplayName("✌ Deve atualizar um usuário!")
    public void deveAtualizarUmUsuario() {
        Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new Usuario(0L, "Julia Sousa Santana", "julia@email.com", "julia123@","-"));
      Usuario usuarioUpdate = new Usuario(usuarioCadastrado.get().getId(), "Julia Santana", "julia_santana@email.com", "julias123@","-" );
        HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuarioUpdate);
        ResponseEntity<Usuario> corpoResposta = testRestTemplate
                .withBasicAuth("root@email.com","root123")
                .exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class);
        assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());

    }
    @Test
    @DisplayName("😁 Deve listar todos os usuários!")
    public void deveMostrarTodosUsuarios(){
        usuarioService.cadastrarUsuario(new Usuario(0L, "Carla Mesquita", "carla_mesquita@email.com", "6543213234","-"));
        usuarioService.cadastrarUsuario(new Usuario(0L, "Marcos Baptista", "marcos1234@email.com", "marcos1234", "-"));

        ResponseEntity<String> resposta = testRestTemplate
                .withBasicAuth("root@email.com","root123")
                .exchange("/usuarios/all", HttpMethod.GET, null, String.class);
        assertEquals(HttpStatus.OK, resposta.getStatusCode());
    }
}
