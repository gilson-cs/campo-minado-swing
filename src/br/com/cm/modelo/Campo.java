package br.com.cm.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Campo {

	private final int linha;
	private final int coluna;
	
	private boolean minado = false;
	private boolean aberto = false;
	private boolean marcado = false;
	
	private List<Campo> vizinhos = new ArrayList<>();
	private List<CampoObservador> observadores = new ArrayList<>();
	
	public Campo(int linha, int coluna) {
		this.linha = linha;
		this.coluna = coluna;
	}
	
	public void registrarObservador(CampoObservador observador) {
		this.observadores.add(observador);
	}
	
	public void notificarObservadores(CampoEvento evento) {
		observadores.stream()
		.forEach(o -> o.eventoOcorreu(this, evento));
	}
	
	public boolean adicionarVizinho (Campo vizinho) {
		
		boolean linhaDiferente = this.linha != vizinho.linha;
		boolean colunaDiferente = this.coluna != vizinho.coluna;
		boolean diagonal = linhaDiferente && colunaDiferente;
		
		int deltaLinha = Math.abs(this.linha - vizinho.linha);
		int deltaColuna = Math.abs(this.coluna - vizinho.coluna);
		int deltaGeral = Math.abs(deltaLinha + deltaColuna);
		
		if(deltaGeral == 1 && !diagonal) {
			vizinhos.add(vizinho);
			return true;
		} else if(deltaGeral == 2 && diagonal) {
			vizinhos.add(vizinho);
			return true;			
		} else {
			return false;
		}
	}
	
	public void alternarMarcacao( ) {
		
		if(!this.aberto) {
			this.marcado = !this.marcado;
		}
		
		if(marcado) {
			notificarObservadores(CampoEvento.MARCAR);
		} else {
			notificarObservadores(CampoEvento.DESMARCAR);
		}
	}
	
	public boolean abrir() {
		
		if(!this.aberto && !marcado) {
			
			if(this.minado) {
				notificarObservadores(CampoEvento.EXPLODIR);
				return true;
			}
			
			setAberto(true);
			
			if(vizinhancaSegura()) {
				this.vizinhos.forEach(v -> v.abrir());
			}
			
			return true;
		} else {
			return false;	
		}
		
	}

	public boolean vizinhancaSegura() {
		return vizinhos.stream().noneMatch(v -> v.minado);
	}
	
	boolean isMarcado () {
		
		return this.marcado;
	}
	
	void minar() {
		
		this.minado = true;
	}
	
	void setAberto(boolean aberto) {
		
		this.aberto = aberto;
		if(aberto) {
			notificarObservadores(CampoEvento.ABRIR);
		}
	}

	boolean isAberto() {
		
		return this.aberto;
	}
	
	boolean isFechado() {
		
		return !this.aberto;
	}

	public int getLinha() {
		return linha;
	}

	public int getColuna() {
		return coluna;
	}
	
	public boolean isMinado() {
		return minado;
	}

	boolean objetivoAlcancado() {
		
		boolean desvendado = !minado && aberto;
		boolean protegido = minado && marcado;
		return desvendado || protegido;
	}
	
	public int minasNaVizinhanca() {
		return (int) vizinhos.stream().filter(v -> v.minado).count();
	}
	
	void reiniciar() {
		
		aberto = false;
		minado = false;
		marcado = false;
		notificarObservadores(CampoEvento.REINICIAR);
	}
	
 }
