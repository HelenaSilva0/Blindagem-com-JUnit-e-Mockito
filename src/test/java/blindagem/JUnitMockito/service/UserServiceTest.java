package blindagem.JUnitMockito.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import blindagem.JUnitMockito.domain.Usuario;
import blindagem.JUnitMockito.exception.RegraNegocioException;
import blindagem.JUnitMockito.repository.UsuarioRepository;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    UsuarioRepository repository;

    @InjectMocks
    UsuarioService service;

    private Usuario usuarioValido() {
        return new Usuario(
                "helena",
                "helena@teste.com",
                "12345678901",
                "Rua dos tolos, 0",
                "senhaForte"
        );
    }

    @Test
    void deveCadastrarUsuarioComSucesso() {
        Usuario u = usuarioValido();

        when(repository.existePorEmail(u.getEmail())).thenReturn(false);
        when(repository.existePorCpf(u.getCpf())).thenReturn(false);

        service.cadastrar(u);

        verify(repository, times(1)).salvar(u);
    }

    @Test
    void deveDeletarUsuarioComSucessoQuandoExistir() {
        String cpf = "12345678901";
        when(repository.existePorCpf(cpf)).thenReturn(true);

        service.deletarPorCpf(cpf);

        verify(repository, times(1)).deletarPorCpf(cpf);
    }

    @Test
    void naoDeveCadastrarQuandoNomeForVazio() {
        Usuario u = new Usuario("", "a@a.com", "12345678901", "Rua X", "senhaForte");

        assertThrows(RegraNegocioException.class, () -> service.cadastrar(u));

        verify(repository, never()).salvar(any());
    }

    @Test
    void deveLancarExceptionQuandoEmailForDuplicado() {
        Usuario u = usuarioValido();
        when(repository.existePorEmail(u.getEmail())).thenReturn(true);

        assertThrows(RegraNegocioException.class, () -> service.cadastrar(u));

        verify(repository, never()).salvar(any());
    }

    @Test
    void deveLancarExceptionQuandoCpfForDuplicado() {
        Usuario u = usuarioValido();
        when(repository.existePorEmail(u.getEmail())).thenReturn(false);
        when(repository.existePorCpf(u.getCpf())).thenReturn(true);

        assertThrows(RegraNegocioException.class, () -> service.cadastrar(u));

        verify(repository, never()).salvar(any());
    }

    @Test
    void deveLancarExceptionQuandoCpfForInvalidoTamanhoIncorreto() {
        Usuario u = new Usuario("Ana", "ana@teste.com", "123", "Rua A", "senhaForte");

        assertThrows(RegraNegocioException.class, () -> service.cadastrar(u));

        verify(repository, never()).salvar(any());
    }

    @Test
    void deveLancarExceptionQuandoSenhaForFraca() {
        Usuario u = new Usuario("Ana", "ana@teste.com", "12345678901", "Rua A", "1234567");

        when(repository.existePorEmail(u.getEmail())).thenReturn(false);
        when(repository.existePorCpf(u.getCpf())).thenReturn(false);

        assertThrows(RegraNegocioException.class, () -> service.cadastrar(u));

        verify(repository, never()).salvar(any());
    }

    @Test
    void naoDeveCadastrarQuandoEnderecoForVazio() {
        Usuario u = new Usuario("Helena", "Helena@teste.com", "12345678901", "   ", "senhaForte");

        assertThrows(RegraNegocioException.class, () -> service.cadastrar(u));

        verify(repository, never()).salvar(any());
    }

    @Test
    void naoDeveDeletarUsuarioInexistente() {
        String cpf = "12345678901";
        when(repository.existePorCpf(cpf)).thenReturn(false);

        assertThrows(RegraNegocioException.class, () -> service.deletarPorCpf(cpf));

        verify(repository, never()).deletarPorCpf(any());
    }
}
