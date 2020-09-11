Relatório CRUD

Trabalho implementado com sucesso.

Observações: 
a) No código Teste.java foi necessário adicionar "<Livro>" na declaração de "CRUD <> arqLivros;". Sem isso não foi possível compilar.

b) Apesar de ser possível chamar read() dentro de delete() e delete() dentro de update(), é necessário "andar" novamente dentro do arquivo, vez que os ponteiros não são compartilhados entre os métodos e não foi prevista uma forma de retornar a posição do ponteiro.

c) Foi pensado em manter o arquivo aberto após a construção do objeto CRUD de forma a ter apenas um ponteiro. Mas para assegurar a escrita, foi preferível abrir e fechar o arquivo em cada método.