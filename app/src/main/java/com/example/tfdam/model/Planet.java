package com.example.tfdam.model;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;

public class Planet {
    @SerializedName("name")
    private String nombre;
    @SerializedName("distance")
    private String distancia;
    @SerializedName("gravity")
    private String gravedad;
    @SerializedName("image")
    private String imagen;

    private Bitmap bImagen;

    public Bitmap getbImagen() {
        return bImagen;
    }

    public void setbImagen(Bitmap bImagen) {
        this.bImagen = bImagen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDistancia() {
        return distancia;
    }

    public void setDistancia(String distancia) {
        this.distancia = distancia;
    }

    public String getGravedad() {
        return gravedad;
    }

    public void setGravedad(String gravedad) {
        this.gravedad = gravedad;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
