SCRIPT_START
{
LVAR_INT ponteiro ponteiro_label este_app
LVAR_FLOAT pos_x pos_y
//não altere valores das variáveis acima
LVAR_FLOAT tam_x tam_y
LVAR_INT var scplayer int
LVAR_FLOAT x y z angulo

IF ponteiro = 0
    TERMINATE_THIS_CUSTOM_SCRIPT
ENDIF

GET_PLAYER_CHAR 0 scplayer
GET_FIXED_XY_ASPECT_RATIO (20.0 20.0) (tam_x tam_y)//ajustar tamanho do ícone

/*
escreva -3 na variável "ponteiro" para esconder o celular da tela (4 bits)
escreva -2 na variável "ponteiro" para parar de usar o celular
escreva -1 na variável "ponteiro" para abrir o menu principal do celular
ex: WRITE_MEMORY ponteiro 4 -1 FALSE
*/

//carregar suas texturas, incluindo ícone do app ~r~
LOAD_TEXTURE_DICTIONARY SaveApp
LOAD_SPRITE 1 "save_icon"

pegar_celular:
    //celular fora da mão
    WHILE NOT var = -1
        READ_MEMORY ponteiro 4 FALSE var
        WAIT 0
    ENDWHILE

    //no menu
    WHILE var = -1
        WAIT 0
        READ_MEMORY ponteiro 4 FALSE var
        USE_TEXT_COMMANDS 1
        DRAW_SPRITE 1 pos_x pos_y tam_x tam_y 225 225 225 255//desenha o ícone do app
        USE_TEXT_COMMANDS 0
    ENDWHILE
    IF NOT var = este_app
        GOTO pegar_celular
    ENDIF
    IF IS_CHAR_IN_ANY_CAR scplayer
        WRITE_MEMORY ponteiro 4 -1 FALSE
        GOTO pegar_celular
    ENDIF

    app_aberto:
    CLEO_CALL salvar_posição 0 (x y z angulo) (int)
    CLEO_CALL fast_teleport 0 (8) (0.0 0.0 0.0)
    WAIT 0
    ACTIVATE_SAVE_MENU
    WAIT 0
    CLEO_CALL fast_teleport 0 (int) (x y z)
    SET_CHAR_HEADING scplayer angulo
    WRITE_MEMORY ponteiro 4 -1 FALSE
GOTO pegar_celular
}
{
    LVAR_INT scplayer n g p i int
    LVAR_FLOAT f[4]
    salvar_posição:
        GET_PLAYER_CHAR 0 scplayer
        GET_CHAR_COORDINATES scplayer f[0] f[1] f[2]
        GET_CHAR_HEADING scplayer f[3]
        //f[2] -= 0.5
        REPEAT 4 n
            g = 1030 + n
            GOSUB escrever
        ENDREPEAT
        GET_CHAR_AREA_VISIBLE scplayer int
        WRITE_MEMORY 0x469D86 4 (int) 0
    CLEO_RETURN 0 f[0] f[1] f[2] f[3] int

    escrever:
        READ_MEMORY 0x00468D5E 4 0 (p)
        i = g * 4
        p += i
        WRITE_MEMORY p 4 (f[n]) 0
    RETURN
}
{
    LVAR_INT int
    LVAR_FLOAT x y z[3]
    LVAR_INT scplayer var
    fast_teleport:

    GET_PLAYER_CHAR 0 scplayer
    IF x = 0.0 
        SET_CHAR_AREA_VISIBLE scplayer 8
        SET_AREA_VISIBLE 8
        x = 2369.545410
        y = -1131.693115
        z[0] = 1050.875000
    ELSE
    ENDIF
    SET_CHAR_AREA_VISIBLE scplayer int
    SET_AREA_VISIBLE int
    LOAD_SCENE x y 100.0
    REQUEST_COLLISION x y
    GET_GROUND_Z_FOR_3D_COORD x y 900.0 z[1]
    GET_WATER_HEIGHT_AT_COORDS x y 0 z[2]
    WHILE z[1] = 0.0
    AND var = 0
        LOAD_SCENE x y 100.0
        REQUEST_COLLISION x y
        GET_GROUND_Z_FOR_3D_COORD x y 900.0 z[1]
        GET_WATER_HEIGHT_AT_COORDS x y 0 z[2]
        IF z[2] >= 0.0
            var = 1
        ENDIF
    ENDWHILE
    IF z[1] = 0.0
        IF z[1] < z[2]
            timera = 0
            WHILE NOT z[1] > z[2]
                WAIT 0
                SET_CHAR_COORDINATES scplayer x y -100.0
                GET_CHAR_COORDINATES scplayer x y z[1]
                IF timera > 500
                    RETURN
                ENDIF
            ENDWHILE
        ELSE
            GET_GROUND_Z_FOR_3D_COORD x y z[0] z[0]
            SET_CHAR_COORDINATES scplayer x y z[0]
        ENDIF
    ELSE
        GET_GROUND_Z_FOR_3D_COORD x y z[0] z[0]
        SET_CHAR_COORDINATES scplayer x y z[0]
    ENDIF
CLEO_RETURN 0
}
SCRIPT_END