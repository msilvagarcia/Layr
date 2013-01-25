# Layr - Faça aplicações web mais legíveis e fáceis de se manutenir

## O que é?

O Layr é uma ferramenta open-source com foco em Free-Logic Markup, modularização e componentização, cujo o intuito é encurtar o tempo de desenvolvimento e manutenção de aplicações Web.

## Por que usar Layr?

 - **Re-aproveitamento de códigos do front-end**: Aplicações desenvolvidas com o Layr utilizam-se de algumas técnicas do XHTML para enriquecer o HTML/HTML5 e re-aproveitar trechos utilizados com frequência em suas aplicações;
 - **Rápida manutenção**: Seu design de software limpo te ajuda a manter suas lógicas de negócio completamente desvinculadas do front-end, facilitando a manutenção e evolução da aplicação;
 - **Convention over configuration**: Layr transformou dois conceitos de desenvolvimento web em convenção: navegação intuitiva e roteamento baseado em negócio. Desta forma não é necessário configurações em arquivos externos, diminuindo o tempo de setup da aplicação.

## Por que Free-Logic Markup ?

O paradigma mais comum de desenvolvimento de software (web) são as tão difundidas Server Pages. Quando este paradigma foi projetado os navegadores eram menos poderosos, e a maior parte do conteúdo dinâmico precisava ser processado pelo servidor, que trazia a página renderizada. Interações JavaScript eram raras, e normalmente tratavam apenas operações de mouse e teclado.

O tempo passou e os navegadores evoluiram. Deixou-se de pensar em páginas e passou-se a pensar em aplicações que rodam no navegador. Muitas das aplicações passaram a tratar cada página em que o usuário interage com o conteúdo como visões, onde cada visão tem sua experiência tratada caso a caso para melhor expor o conteúdo ao usuário. Neste modelo, cada detalhe aplicado a tela requer cada vez mais dos recursos do navegador. Ao trabalhar-se livre de lógica no markup, nossos HTML's passam a focar apenas na apresentação do conteúdo, e deixamos a interação sob controle total do JavaScript dos navegadores.

As vantagens deste paradigma sobre as Server Pages são enúmeras. A principal delas é o fato de que com as regras separadas da marcação é possível re-aproveitar lógicas que se repetem com frequencia na interface. A manutenção da interface fica tão simples que qualquer pessoa com conhecimento em HTML e CSS consegue dar manutenção É possível isolar lógicas de interface e criar testes unitários que garantam a sua integridade, não nos obrigando a recorrer a testes de integração para ter os mesmos resultados.


## Por que XHTML?

Sempre que incluímos uma biblioteca nova durante a construção do software, incluímos também mais um ponto de manutenção no código fonte. O mesmo acontece com as bibliotecas de template. Além do fato de que, ao longo do tempo, com a pouca adoção de algumas bibliotecas, elas podem cair em desuso. Se corremos riscos trabalhando com modelos que possam ter pouca adoção, por que não utilizar o HTML, que todo navegador é capaz de interpretar?

Utilizar-se do modelo XHTML sobre o modelo padrão do HTML tem vantagens também. O modelo bem definido de XML, permite dar versatilidade aos editores de texto ao se editar um HTML ( auto-identação e auto-complete, por exemplo). É através desta junção ( sintaxe XML + HTML ) que conseguimos mais facil a diagramação da tela e reutilização de lógicas de telas.

## É possível gerar HTML5 mesmo usando XHTML?

Layr vale-se apenas da notação XML do XHTML. Isso significa que se todas as tags forem corretamente fechadas, e termos apenas um único nós raíz, teremos um HTML5 válido e componentizável. Inclusive, o DOCTYPE padrão do Layr é o do HTML5 ```<!DOCTYPE html>```.

## Componentização de modo simples e fácil

O formulário abaixo foi escrito usando a taxonomia do tema Bootstrap ( do Twitter ).

```xhtml
<div xmlns="http://www.w3.org/1999/xhtml">

  <form class="form-horizontal" action="/user/save" method="POST">
    <div class="control-group">
      <label class="control-label">Email</label>
      <div class="controls">
        <input type="text" name="inputEmail" placeholder="Email">
      </div>
    </div>
    <div class="control-group">
      <label class="control-label">Password</label>
      <div class="controls">
        <input type="password" name="inputPassword" placeholder="Password">
      </div>
    </div>
    <div class="control-group">
      <div class="controls">
        <label class="checkbox">
          <input type="checkbox"> Remember me
        </label>
        <button type="submit" class="btn">Sign in</button>
      </div>
    </div>
  </form>

</div>
```

Note que as tags que representam um item do formulário está se repetindo com frequência. Memorizar as classes CSS e sua estrutura nem sempre é fácil. Mas, e se pudessemos usar uma sintaxe mais amigável que nos permita re-utilizar estas estruturas sem repetição de código?

```xhtml
<div xmlns="http://www.w3.org/1999/xhtml"
     xmlns:ui="urn:components">

  <ui:HorizontalForm action="/user/save" method="POST">

    <ui:FormItem label="Email:">
      <ui:TextField name="inputEmail" placeholder="Email" />
    </ui:FormItem>

    <ui:FormItem label="Password:">
      <ui:PasswordField name="inputPassword" placeholder="Password" />
    </ui:FormItem>

    <ui:FormItem>
      <ui:Checkbox label="Remember me" name="rememberme" />
      <ui:SubmitButton label="Sign in" />
    </ui:FormItem>

  </ui:HorizontalForm>

</div>
```

Você deve ter notado que a quantidade de código escrita diminuiu um pouco. Graças aos namespaces temos a capacidade de criar tags customizadas sem ferir o HTML padrão. Para isso, foram criados vários arquivos .xhtml com os seus antigos conteúdos. O nome da tag deve ser o nome do arquivo. A localização destes arquivos foram definidos através do namespace **ui**, especificada na tag raíz do documento. Através da notação urn, definifimos que eles ficarão na pasta **components** ( raíz do class-path da aplicação* ). Veja como ficou o corpo do componente _FormItem_:

```xhtml
<div xmlns="http://www.w3.org/1999/xhtml"
   xmlns:tpl="urn:layr:template"
    class="FormItem control-group">

  <label class="pull-left">#{FormItem:label}</label>
  <tpl:children />

</div>
```

O componente **children** define o local aonde os filhos do componente serão renderizados. Esta tag foi desenvolvida através do conjunto de tags ( TagLibs ) de template ( definido pelo namespace **tpl** ). Esta TagLib acompanha o Layr e foi desenhada para auxliar no desenvolvimento de suas telas. Você pode usar esta mesma abordagem para traduzir qualquer conjunto de tags em componentes, não há limitação de tamanho ou quantidade de componente.

## Organização Natural

Definir pacotes (aka. Packages ou Namespaces) para isolar escopo, pastas para agrupar determinadas views em HTML e subformários, são práticas diárias dos programadores. Ao se ler um código fonte bem organizado encontramos rapidamente o que precisamos durante uma manutenção, e conseguimos definir melhores estratégias de _refactoring_ e evolução do software. E é esta organização, que é feita as vezes de maneira tão natural, um dos principais responsáveis por isso.

Para entender como o Layr faz o roteamento de seus HTML's é importante entender que ele se vale basicamente de dois conceitos: **Navegação Natural** e **Navegação Condicionada ao Negócio**. Ambos são conceitos que seguem o seguinte princípio: se nos dedicamos tanto tempo organizando nossos códigos fontes, por que não podemos nos valer desta organização para o roteamento interno de nosso software?

## Navegação Natural ( Natural Routing )

Vamos imaginar que sua aplicação roda no URL http://localhost:8080/, e que sua estrutura de projeto prevê que a raíz do aplicativo está numa pasta chamada **source**. Abaixo um exemplo de como poderia estar o seu projeto usando o Layr:

<pre>
  |-- source
  |   |-- user
  |   |   |-- edit.xhtml
  |   |   |-- view.xhtml
  |   |   |-- add.xhtml
  |   |   |-- list.xhtml
  |   |-- theme
  |   |   |-- theme.xhtml
  |   |   |-- theme.css
  |   |-- components
  |   |   |-- FormItem.xhtml
  |   |   |-- Checkbox.xhtml
  |   |   |-- TextField.xhtml
  |   |   |-- PasswordField.xhtml
</pre>

Durante todo o desenvolvimento de um projeto, investe-se tempo organizando e definindo boas taxonomias para seus arquivos. Com o tempo, isto se torna um hábito natural do desenvolvedor. Se pudessemos aproveitar este esforço para definir, por exmplo, a maneira com que os _resources_ serão acessados pelo navegador, ou seja, como será definida a URL em que o usuário irá navegar pelo sistema, então ganhariamos tempo de desenvolvimento. A isso, é dado o nome de _Navegação Natural_.

Sendo assim, imagine que seja necessário exibir a tela de listagem de usuários. No modelo proposto acima, você poderia acessá-lo através da URL http://localhost:8080/user/list/.

### Obtendo dados para a interface

Note que no exemplo anterior o foco era apenas a camada de apresentação, a definição de como ela será exibida. Aqui entra o conceito de **Free-Logic Markup**, apresentado anteriormente, cabendo às rotinas JavaScript associadas a esta view tratar operações de busca de dados do servidor (JSON, XML, etc), definir os eventos a serem disparados de acordo com a interação com o usuário, ou qualquer outra operação dinâmica da tela.

Existem hoje no mercado várias ferramentas que auxiliam estas interações do usuário, e foge um pouco ao escopo deste documento detalhar o seu uso. Mas para fins de futuras pesquisas, os frameworks abaixo serão de grande valia quando for se trabalhar com **Free-Logic Markup**:

 - [KnockoutJS](http://knockoutjs.com)
 - [jQuery](http://jquery.com)
 - [MooTools](http://mootools.net)
 - [Backbone.js](http://backbonejs.org)
 - [Underscore.js](http://underscorejs.org)
 - [AngularJS](http://angularjs.org)

## Navegação Condicionada ao Negócio ( Business Routing )

Nem sempre é possível utilizar apenas Navegação Natural. As vezes, faz-se necessário consultar algum dado do banco de dados para só então devolver a informação para o navegador. A estas requisições damos o nome de **Navegação Condicionada ao Negócio**.

Para diferenciar este tipo de requisição de uma requisição de _obtenção de dados para a interface_, vamos imaginar que um cliente de seu software foi selecionado para testar uma tela nova do sistema. Quando ele entrar no URL ```http://localhost:8080/product/list/```, ele deveria ver a versão nova da tela de listagem de produtos, enquanto os usuários antigos deveriam continuar vendo a página antiga.

Para tratar este tipo de situação com o Layr, criamos a classe abaixo:

```java
@WebResource("/product/")
public class HomeResource {

  Products products;
  UserSession userSession; // A sample code that represents the current logged in user
  String productListTemplateName;

  @Route(
    pattern="/list/",
    template="/products/#{productListTemplateName}.xhtml")
  public void listProducts(){
    if ( userSession.isBetaUser() )
      productListTemplateName = "listBeta";
    else
      productListTemplateName = "list";
  }

}
```

No exemplo acima, desenhamos uma rota de negócio para ```/product/list/```. No método _listProducts_ verificamos se o usuário é beta, se for verdadeiro definimos que o template a ser renderizado é ```/products/listBeta.xhtml```, do contrário, renderizamos ```/products/list.xhtml```. Note que em momento algum foi incluída lógicas de interface no template, apenas tomamos uma _decisão de negócio_ para definir qual template deve ser enviado ao navegador.

## API de Navegação Condicionada ao Negócio ( Business Routing API )

### Mapeamento

Para mapear uma rota no Layr, deve-se criar uma classe qualquer e anotá-la com ```@WebResource```, a esta classe chamamos de **resource**. O valor padrão da anotação espera o caminho raiz ( dentro do contexto ) para uma rota. Em nosso exemplo, ```/product/```.

Para finalizar, deve-se apontar qual método será executado quando uma requisição chegar. Basta incluir a anotação ```@Route``` no método. Por padrão, se nada for informado ao atributo ```pattern```, a rota fica com o nome do método (em nosso exemplo, ficaria /product/listProducts). Do contrário o valor do pattern é utilizado.

### Enviando dados para o WebResource

É possível receber os dados enviados via parâmetro na URL ( ou num POST de Content-Type '' ) em seu _resource_. Basta que, para cada parâmetro que deseja ser mapeado, crie-se um parâmetro no método a ser executado. Para ficar mais claro vamos ao exemplo abaixo:

```java

@WebResource("/customer/")
public class CustomerResource {

  @Route
  public void save(
    @Parameter("companyId") String companyId,
    @Parameter("user") User user
  ){
    log( "Company Id:" + companyId );
    saveUser( user );
  }

  /* other usefull methods here */

  // Class User

  public class User {

    Long id;
    String firstName;
    String lastName;

  }

}

```

No exemplo acima criamos uma rota que irá criar um novo cliente no banco. Neste exemplo, quando este método for executado, não vai ser retornado valor algum.

Para que o companyId chegue no método bastaria incluir no URL ( ou enviar via POST ) um parâmetro _companyId=123_, por exemplo. É possível enviar qualquer objeto como parâmetro deste jeito, exceto objetos com Generics (trataremos sobre isso mais adiante).

No caso do parâmetro _user_, que é representa pelo tipo User, há duas formas de enviar o valor:
 - Enviar no formato JSON ( é normalmente o mais modo mais conveniente ): ```user={ firstName: "Helden", lastName="Teixeira" }```
 - Outro modelo seria enviar cada campo como parâmetro, ```user.firstName=Helden&user.lastName=Teixeira```. Neste modelo a limitação é a impossibilidade de se enviar itens de listas ( primitivas ou não ).

Para finalizar, o código JavaScript abaixo exemplifica como seria possível enviar estes dados via JSON para o servidor utilizando a biblioteca [jQuery](http://jquery.com).

```javascript

  var currentCompanyId = 123;
  var user = {
    firstName: "Helden",
    lastName: "Teixeira"
  };

  function notifyThatUserHasBeenSaved(){
    alert( "User '" + user.lastName + ", " + user.firstName + "' has been created!" );
  }

  $.post({
    url: "/customer/save",
    data: {
      companyId: currentCompanyId,
      user: JSON.stringify( user )
    },
    success: notifyThatUserHasBeenSaved
  })

```

### Placeholders

O Layr se vale de _placeholders_ para serem substituídas pelos valores de variáveis no corpo da classe com as rotas. No exemplo que demos sobre a navegação condicionada ao negócio, utilizamos um placeholder no atributo _template_ da definição da rota. Neste exemplo, sempre que o URL ```http://localhost:8080/product/list/``` for chamado, o método listProdutcs

 Redirecionando o usuário de acordo com uma regra de negócio

No exemplo abaixo, precisávamos impedir que um usuário com pendencias de pagamento acessasse a uma determinada parte do sistema que era de acesso exclusivo para usuários pagantes.

```java
@WebResource("/user/")
public class UserResource {

  String redirectTo;
  
  @Route(
    pattern="/#{id}/edit", template="user/editForm.xhtml",
    redirectTo="#{redirectTo}" )
  public void editUser( @Parameter("id") Long userId ) {
    if ( haveUserBillingPendencies() ){
      redirectTo = "/user/warning/";
      return;
    }
    
    // load user information with userId.
  }

  public boolean haveUserBillingPendencies(){
    return new Date().getTime() % 2 == 0;
  }

}
```

Ao definirmos uma rota com o atributo ```redirectTo```, o Layr tentará redirecionar o usuário para a tela nova. Em nosso exemplo, colocamos a referência de uma variável no atributo. Após executar a 
