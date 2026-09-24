package tarefas02.api.controllers; // Declaração do pacote onde a classe controller está localizada

import java.net.URI; // Importa a classe URI para construir e manipular HTTP de novos recursos

import org.springframework.beans.factory.annotation.Autowired; // injção automatica do Spring
import org.springframework.http.ResponseEntity; // impora a classe para montar a resposta HTTP completa(status, headers, corpo)
import org.springframework.validation.annotation.Validated; // importa anotação para habiltar suporte a validação no controller
import org.springframework.web.bind.annotation.DeleteMapping; // mapeia requisições do tipo delete
import org.springframework.web.bind.annotation.GetMapping; // mapeia requisições do tipo GET
import org.springframework.web.bind.annotation.PathVariable; // mapeia variaveis passadas diretamente via caminho da URL
import org.springframework.web.bind.annotation.PostMapping; // mapeia requisições do tipo POST
import org.springframework.web.bind.annotation.PutMapping; // mapeia requisições do tipo PUT
import org.springframework.web.bind.annotation.RequestBody; // converste objetos JSON em ojetos JAVA
import org.springframework.web.bind.annotation.RequestMapping; // importa anotação para definir o caminho/rota bas do controlador
import org.springframework.web.bind.annotation.RestController; //  importa anotação que define esta classe como um controller REST
import org.springframework.web.servlet.support.ServletUriComponentsBuilder; // Importa utilitario para gerar a URI da requisição atual dinamicamente.

import tarefas02.api.models.User;
import tarefas02.api.models.User.CreateUser;
import tarefas02.api.models.User.UpdateUser;
import tarefas02.api.services.UserService;
import org.springframework.web.bind.annotation.RequestParam;


@RestController  // Define a classe como um controlador REST que retorna respostas em JSON
@RequestMapping ("/user") // Define que toas as rotas desta classe terão como prefixo o caminho "/user"
@Validated //Ativa a verificação de validações nos parametros recebidos no controller

public class UserController {

    @Autowired 
    private UserService userService;

    @GetMapping ("/{id}") // Mapeia requisições HTTP GET na rota "/user/{id}"
    public ResponseEntity<User> findById(@PathVariable Long id){ // Método para buscar usuario por id capturado da URL
        User obj=this.userService.findById(id); // Invoca a busca do usuário através do ID recebido
        return ResponseEntity.ok().body(obj); // Retorn código HTTP 200(ok) com o onjeto User no corpo da resposta
    } // Fim do método FindbyId

    @PostMapping // Mapeia requisições HTTP POST  na rota base"/user"(criação de nocvo usuário)
    public ResponseEntity<Void> create(@Validated (CreateUser.class) @RequestBody User obj){ // Valida regra de CreateUser e desserializa o corpo JSON
        this.userService.create(obj); // Chama a camada de serviço para persistir o novo usuario no banco de dados. 
        URI url = ServletUriComponentsBuilder.fromCurrentRequest() // Obtém a rota da requisição atual 
            .path("/{id}").buildAndExpand(obj.getId()).toUri(); // Adiciona o Id do usuario gerado no final do caminho da URI
            return ResponseEntity.created(url).build(); //Retorna código HTTP 201(created) contendo a URL no cabeçalho location
    }

    @PutMapping("/{id}")// Mapeia requisições HTTP PUT na rota base "/user/{id}" (atualização do usuário)
    public ResponseEntity<Void> update(@Validated(UpdateUser.class)@RequestBody User obj, @PathVariable Long id){ // Aplica a regra de Updateuser e recebe ID e JSON
        obj.setId(id); // Garante que o ID do objeto a ser atualizado corresponde ao ID informado no parametro da URL
        this.userService.update(obj); // Executa a atualização da senha do usuário no banco de dados
        return ResponseEntity.noContent().build();//Retorno código HTTP 204(No content)indicando sucesso sem corpo de resposta
    }

    @DeleteMapping ("/{id}") //mapeia requisições HTTP DELETE na rota "/user/{id}" (exclusão de usuário)
    public ResponseEntity<Void> delete(@PathVariable Long id){ // Captura o ID da URL a ser deletado
        this.userService.delete(id); // Invoca o método de deleção so serviço
        return ResponseEntity.noContent().build();//Retorna códigoHTTP 204 (no content) confirmando a exclusão
            }
    }
    