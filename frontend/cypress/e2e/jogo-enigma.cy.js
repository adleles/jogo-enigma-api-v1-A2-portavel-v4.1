describe('Jogo Enigma E2E - Homologacao',()=>{
 it('1 - abre a home via BFF',()=>{cy.visit('/');cy.contains('Jogo Enigma');cy.contains('Front → BFF Node.js')});
 it('2 - cadastra participante ponta a ponta',()=>{cy.visit('/');const id=Date.now();cy.get('#nome').type('Ana E2E');cy.get('#email').type(`ana${id}@teste.com`);cy.get('#pontos').clear().type('80');cy.get('button').click();cy.get('#msg').should('contain','Cadastrado com sucesso');cy.get('#lista').should('contain','Ana E2E').and('contain','MESTRE')});
 it('3 - rejeita email duplicado via API/BFF',()=>{const email=`dup${Date.now()}@teste.com`;const body={nome:'Dup',email,pontos:10};cy.request('POST','/bff/participantes',body).its('status').should('eq',201);cy.request({method:'POST',url:'/bff/participantes',body,failOnStatusCode:false}).its('status').should('eq',400)});
});
