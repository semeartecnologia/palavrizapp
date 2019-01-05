package com.semear.tec.palavrizapp.utils;

import com.semear.tec.palavrizapp.models.Groups;
import com.semear.tec.palavrizapp.models.Themes;

import java.util.ArrayList;
import java.util.List;

public class InitData {

    public static final List<Groups> groups = new ArrayList<Groups>(){{
        add(new Groups(1, "Meio Ambiente", "Preservação Ambiental"));
        add(new Groups(2, "Direitos Humanos (Minorias ou Pessoas)", "Direitos Humanos no Brasil"));
        add(new Groups(3, "Direitos Sociais", "Direitos Sociais no Brasil"));
        add(new Groups(4, "Política", "Participação Política"));
        add(new Groups(5, "Comportamentos", "Comportamentos"));
    }};

    public static final List<Themes> themes = new ArrayList<Themes>(){{
        add(new Themes(1, 1, "Água"));
        add(new Themes(2, 1, "Lixo"));
        add(new Themes(3, 1, "Fontes de Energia"));
        add(new Themes(4, 1, "Poluição"));
        add(new Themes(5, 1, "Mudanças Climáticas"));
        add(new Themes(6, 1, "Progresso e Meio Ambiente"));
        add(new Themes(7, 1, "Extinção de Espécies"));
        add(new Themes(8, 1, "Desastres Ambientais"));

        add(new Themes(9, 2, "Intensidade Cultural"));
        add(new Themes(10, 2, "Povos Indígenas"));
        add(new Themes(11, 2, "Violência Juvenil"));
        add(new Themes(12, 2, "Jovens e Crianças em situação de abandono/risco"));
        add(new Themes(13, 2, "Intolerância na Internet"));
        add(new Themes(14, 2, "Intolerância de gênero"));
        add(new Themes(15, 2, "Patrimônio Cultural"));
    }};

}
