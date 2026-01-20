package blindagem.JUnitMockito.service;

import blindagem.JUnitMockito.domain.Usuario;
import blindagem.JUnitMockito.exception.RegraNegocioException;
import blindagem.JUnitMockito.repository.UsuarioRepository;

public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public void cadastrar(Usuario usuario) {
        if (usuario == null) throw new RegraNegocioException("Usuario nao pode ser nulo");

        validarCampo(usuario.getNome(), "Nome");
        validarCampo(usuario.getEmail(), "Email");
        validarCampo(usuario.getCpf(), "CPF");
        validarCampo(usuario.getEndereco(), "Endereco");
        validarCampo(usuario.getSenha(), "Senha");

        if (repository.existePorEmail(usuario.getEmail())) {
            throw new RegraNegocioException("Email ja existente");
        }

        if (!usuario.getCpf().matches("\\d{11}")) {
            throw new RegraNegocioException("CPF deve ter exatamente 11 digitos");
        }

        if (repository.existePorCpf(usuario.getCpf())) {
            throw new RegraNegocioException("CPF ja existente");
        }

        if (usuario.getSenha().length() < 8) {
            throw new RegraNegocioException("Senha deve ter no minimo 8 caracteres");
        }

        repository.salvar(usuario);
    }

    public void deletarPorCpf(String cpf) {
        validarCampo(cpf, "CPF");

        if (!cpf.matches("\\d{11}")) {
            throw new RegraNegocioException("CPF deve ter exatamente 11 digitos");
        }

        if (!repository.existePorCpf(cpf)) {
            throw new RegraNegocioException("Usuario nao encontrado para delecao");
        }

        repository.deletarPorCpf(cpf);
    }

    private void validarCampo(String valor, String nomeCampo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new RegraNegocioException(nomeCampo + " nao pode ser vazio");
        }
    }
}