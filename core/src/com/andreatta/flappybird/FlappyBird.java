package com.andreatta.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.awt.Color;
import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture[] passaros;
    private Texture fundo;
    private Texture canoBaixo;
    private Texture canoTopo;
    private Texture gameOver;
    private Random numeroRandomico;
    private BitmapFont fonte;
    private BitmapFont txtReiniciar;
    private Circle passaroCiruclo;
    private Rectangle retanguloCanoTopo;
    private Rectangle retanguloCanoBaixo;
    private ShapeRenderer shape;

    private float larguraDispositivo=0;
    private float alturaDispositivo=0;
    private int estadoJogo = 0;
    private boolean marcouPonto = false;
    private int pontuacao = 0;

    private float variacao = 0;
    private float velocidadeQueda;
    private float posicaoInicialVertical;
    private float posicaoMovimentoCanoHorizontal;
    private float espacoEntreCanos;
    private float deltarTIme;
    private float AlturaEntreCanosRandomico;

    //Camera
    private OrthographicCamera camera;
    private Viewport viewport;
    private final float VIRTURAL_WIDTH = 768;
    private final float VIRTUAL_HEIGHT = 1024;

	@Override
	public void create () {
		batch = new SpriteBatch();
        passaroCiruclo = new Circle();
        //retanguloCanoTopo = new Rectangle();
        //retanguloCanoBaixo = new Rectangle();
        numeroRandomico = new Random();
        fonte = new BitmapFont();
        txtReiniciar = new BitmapFont();
        txtReiniciar.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        txtReiniciar.getData().setScale(3);
        fonte.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        fonte.getData().setScale(6);

		passaros = new Texture[3];
		passaros[0] = new Texture("passaro1.png");
		passaros[1] = new Texture("passaro2.png");
		passaros[2] = new Texture("passaro3.png");
		fundo = new Texture("fundo.png");
        gameOver = new Texture("game_over.png");

		canoBaixo = new Texture("cano_baixo.png");
		canoTopo = new Texture("cano_topo.png");

        alturaDispositivo = VIRTUAL_HEIGHT;
        larguraDispositivo = VIRTURAL_WIDTH;

        posicaoInicialVertical = alturaDispositivo/2;
        posicaoMovimentoCanoHorizontal = larguraDispositivo;
        espacoEntreCanos = 300;

        //Camera

        camera = new OrthographicCamera();
        camera.position.set(VIRTURAL_WIDTH/2,VIRTUAL_HEIGHT/2,0);
        viewport = new StretchViewport(VIRTURAL_WIDTH,VIRTUAL_HEIGHT,camera);
	}

    //Chado de tempos em tempos para formar o jogo
    //Parar criar o efeito de animação
	@Override
	public void render () {

	    camera.update();

	    //limpar framas anteriores
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        deltarTIme =  Gdx.graphics.getDeltaTime();
        variacao+= deltarTIme * 10;
        if(variacao > 2){
            variacao = 0;
        }

	    if(estadoJogo == 0){
            if(Gdx.input.justTouched()){
                estadoJogo = 1;
            }
        }else {//Jogador jogando

            velocidadeQueda++;
            if(posicaoInicialVertical > 0 || velocidadeQueda < 0)
                posicaoInicialVertical = posicaoInicialVertical - velocidadeQueda;

            if(estadoJogo == 1){

                posicaoMovimentoCanoHorizontal -= deltarTIme * 250;
                if(Gdx.input.justTouched()){
                    velocidadeQueda = -15;
                }

                //Verifica se o cano saiu da tela
                if(posicaoMovimentoCanoHorizontal < -canoTopo.getWidth()){
                    posicaoMovimentoCanoHorizontal = larguraDispositivo;
                    AlturaEntreCanosRandomico = numeroRandomico.nextInt(400) - 200;
                    marcouPonto = false;
                }

                //verifica pontuação
                if(posicaoMovimentoCanoHorizontal < 120){
                    if(!marcouPonto){
                        pontuacao++;
                        marcouPonto = true;
                    }

                }
            }else{
                //Tela gameOver
                if(Gdx.input.justTouched())
                    estadoJogo = 0;
                    pontuacao = 0;
                    velocidadeQueda = 0;
                    posicaoInicialVertical = alturaDispositivo/2;
                    posicaoMovimentoCanoHorizontal = larguraDispositivo;
            }

        }
        //dados de projecao da camera
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        batch.draw(fundo,0,0,larguraDispositivo,alturaDispositivo);
        batch.draw(passaros[(int)variacao],120,posicaoInicialVertical);
        batch.draw(canoTopo,posicaoMovimentoCanoHorizontal,alturaDispositivo/2 + espacoEntreCanos / 2 + AlturaEntreCanosRandomico);
        batch.draw(canoBaixo,posicaoMovimentoCanoHorizontal,alturaDispositivo/2 - canoBaixo.getHeight() - espacoEntreCanos/2 + AlturaEntreCanosRandomico);

        fonte.draw(batch,String.valueOf(pontuacao),larguraDispositivo/2,alturaDispositivo - 50);

        if(estadoJogo == 2){
            batch.draw(gameOver,larguraDispositivo/2 - gameOver.getWidth()/2,alturaDispositivo/2);
            txtReiniciar.draw(batch,"Toque para reiniciar!",larguraDispositivo/2 - 200,alturaDispositivo/2 - gameOver.getHeight() / 2);
        }

        batch.end();

        //Desenhar formas

        passaroCiruclo.set(120 + passaros[0].getWidth()/2 ,posicaoInicialVertical + passaros[0].getHeight()/2, passaros[0].getWidth()/2);
        retanguloCanoBaixo = new Rectangle(
                posicaoMovimentoCanoHorizontal, alturaDispositivo/2 - canoBaixo.getHeight() - espacoEntreCanos/2 + AlturaEntreCanosRandomico,
                canoBaixo.getWidth(),canoBaixo.getHeight()
        );
        retanguloCanoTopo = new Rectangle(
                posicaoMovimentoCanoHorizontal, alturaDispositivo/2 + espacoEntreCanos / 2 + AlturaEntreCanosRandomico,
                canoTopo.getWidth(),canoTopo.getHeight()
        );

       /* shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.circle(passaroCiruclo.x,passaroCiruclo.y,passaroCiruclo.radius);
        shape.rect(retanguloCanoBaixo.x,retanguloCanoBaixo.y,retanguloCanoBaixo.width,retanguloCanoBaixo.height);
        shape.rect(retanguloCanoTopo.x,retanguloCanoTopo.y,retanguloCanoTopo.width,retanguloCanoTopo.height);
        //shape.setColor(com.badlogic.gdx.graphics.Color.RED);
        shape.end();*/

        //teste colisão
        if(Intersector.overlaps(passaroCiruclo,retanguloCanoBaixo)
                || Intersector.overlaps(passaroCiruclo,retanguloCanoTopo)
                || posicaoInicialVertical <= 0 || posicaoInicialVertical >= alturaDispositivo){

            estadoJogo = 2;
        }
	}

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height);
    }
}
