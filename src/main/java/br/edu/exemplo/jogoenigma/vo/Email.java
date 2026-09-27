package br.edu.exemplo.jogoenigma.vo;
import java.util.regex.Pattern;
public record Email(String valor) { private static final Pattern P=Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"); public Email { if(valor==null||!P.matcher(valor).matches()) throw new IllegalArgumentException("E-mail inválido"); } }
