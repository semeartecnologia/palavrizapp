package com.semear.tec.palavrizapp.utils;

import com.semear.tec.palavrizapp.models.Groups;
import com.semear.tec.palavrizapp.models.Themes;
import com.semear.tec.palavrizapp.models.VideoPreview;

import java.util.ArrayList;
import java.util.List;

public class InitData {

    public static final Groups[] getInitialGroups(){
        return new Groups[]{
                new Groups(1, "Meio Ambiente", "Preservação Ambiental"),
                new Groups(2, "Direitos Humanos (Minorias ou Pessoas)", "Direitos Humanos no Brasil"),
                new Groups(3, "Direitos Sociais", "Direitos Sociais no Brasil"),
                new Groups(4, "Política", "Participação Política"),
                new Groups(5, "Comportamentos", "Comportamentos")
        };
    }


    public static ArrayList<VideoPreview> getListVideoPreview(){
        return new ArrayList<VideoPreview>() {{
            add(new VideoPreview(1,"Matrícula", "Veja detallhes sobre a matrícula", "WmSKaVruVrM")); // WmSKaVruVrM
            add(new VideoPreview(2,"Sociedade do Espetáculo", "Sociedade do Espetáculo", "Gxzt-QsoeoM")); //Gxzt-QsoeoM
            add(new VideoPreview(3,"ENEM 1", "Como sua prova é corrigida", "CAr3WF2t658")); // CAr3WF2t658

            add(new VideoPreview(4,"Projeto de texto", "Projeto de texto", "p7GjrTa5Rws"));//p7GjrTa5Rws

        }};
    }

    public static final Themes[] getInitialThemes(){
        return new Themes[]{
                new Themes(1, 1, "Água"),
                new Themes(2, 1, "Lixo"),
                new Themes(3, 1, "Fontes de Energia"),
                new Themes(4, 1, "Poluição"),
                new Themes(5, 1, "Mudanças Climáticas"),
                new Themes(6, 1, "Progresso e Meio Ambiente"),
                new Themes(7, 1, "Extinção de Espécies"),
                new Themes(8, 1, "Desastres Ambientais"),

                new Themes(9, 2, "Intensidade Cultural"),
                new Themes(10, 2, "Povos Indígenas"),
                new Themes(11, 2, "Violência Juvenil"),
                new Themes(12, 2, "Jovens e Crianças em situação de abandono/risco"),
                new Themes(13, 2, "Intolerância na Internet"),
                new Themes(14, 2, "Intolerância de gênero"),
                new Themes(15, 2, "Patrimônio Cultural"),

                new Themes(16, 3, "Educação e tecnologia", "Educação"),
                new Themes(17, 3, "Violência na escola", "Educação"),
                new Themes(18, 3, "Educação, justiça social e desenvolvimento econômico", "Educação"),
                new Themes(19, 3, "Saúde e prevenção de doenças", "Saúde"),
                new Themes(20, 3, "DST e os jovens brasileiros", "Saúde"),
                new Themes(21, 3, "Combate a epidemias/endemias", "Saúde"),
                new Themes(22, 3, "Drogas (ligadas à questão de saúde)", "Saúde")
        };

    }

}
