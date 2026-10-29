package com.inventienda.exception;

public class StockInsuficienteException extends ReglaNegocioException {

    public StockInsuficienteException(String producto, int disponible, int solicitado) {
        super("Stock insuficiente para '" + producto + "': disponible " + disponible
                + ", solicitado " + solicitado);
    }
}
