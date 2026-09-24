// Pacote onde esta classe de serviço está localizada no projeto
package tarefas02.api.services;

// Importa Optional para tratar valores que podem ou não estar presentes (evitando NullPointerException)
import java.util.Optional;

// Importa a anotação do Spring para injeção automática de dependências
import org.springframework.beans.factory.annotation.Autowired;
// Importa a anotação que define esta classe como um componente de serviço gerenciado pelo Spring
import org.springframework.stereotype.Service;
// Importa a anotação para gerenciar transações no banco de dados (garante atomicidade nas operações)
import org.springframework.transaction.annotation.Transactional;

// Importa o modelo/entidade User
import tarefas02.api.models.User;
// Importa o repositório de tarefas para salvar/manipular as tarefas atreladas ao usuário
import tarefas02.api.repositories.TaskRepository;
// Importa a interface do repositório responsável pelas operações de banco de dados do User
import tarefas02.api.repositories.UserRepository;

// Anotação que indica ao Spring que esta classe contém as regras de negócio da entidade User
@Service
public class UserService {

    // Injeta automaticamente a instância do UserRepository gerenciada pelo Spring
    @Autowired
    private UserRepository userRepository;

    // Injeta automaticamente a instância do TaskRepository para salvar tarefas associadas
    @Autowired
    private TaskRepository taskRepository;

    // Método para buscar um usuário específico pelo seu ID
    public User findById(Long id) {
        // Executa a busca no banco; retorna um Optional contendo (ou não) o User
        Optional<User> user = this.userRepository.findById(id);
        
        // Se o usuário existir, retorna o objeto; se estiver vazio, lança uma exceção RuntimeException
        return user.orElseThrow(() -> new RuntimeException(
            "Usuário não encontrado! Id: " + id + ", Tipo: " + User.class.getName()
        ));
    }

    // Garante que o cadastro ocorra dentro de uma transação de banco de dados (rollback automático se falhar)
    @Transactional
    public User create(User obj) {
        // Define o ID como null para garantir que o JPA realize uma inserção (INSERT) e não uma atualização
        obj.setId(null);
        
        // Salva o novo usuário no banco de dados e atualiza 'obj' com o ID gerado
        obj = this.userRepository.save(obj);
        
        // Salva em lote no banco todas as tarefas associadas à lista de tarefas do usuário
        this.taskRepository.saveAll(obj.getTasks());
        
        // Retorna o usuário criado com o ID preenchido e suas tarefas persistidas
        return obj;
    }

    // Garante que a atualização ocorra dentro de uma transação isolada no banco
    @Transactional
    public User update(User obj) {
        // Reaproveita o findById para verificar se o usuário a ser atualizado existe (lança erro se não existir)
        User newObj = findById(obj.getId());
        
        // Atualiza apenas o campo da senha do objeto persistido com o novo valor recebido
        newObj.setPassword(obj.getPassword());
        
        // Salva a alteração no banco de dados e retorna o objeto atualizado
        return this.userRepository.save(newObj);
    }

    // Método para deletar um usuário pelo ID
    public void delete(Long id) {
        // Verifica se o usuário existe antes de tentar deletá-lo (lança exceção se não existir)
        findById(id);
        
        try {
            // Solicita a remoção do usuário no banco de dados pelo ID
            this.userRepository.deleteById(id);
        } catch (Exception e) {
            // Captura exceções (como violações de chave estrangeira, caso existam tarefas vinculadas) e lança mensagem customizada
            throw new RuntimeException("Não é possível excluir pois há entidades relacionadas!");
        }
    }
}