SCRIPT_START
{
// Não terminado   
TERMINATE_THIS_CUSTOM_SCRIPT

LVAR_INT ponteiro ponteiro_label este_app
LVAR_FLOAT pos_x pos_y
//não altere valores das variáveis acima
LVAR_FLOAT tam_x tam_y
LVAR_INT var scplayer

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
LOAD_TEXTURE_DICTIONARY configs
LOAD_SPRITE 1 "configs"

STREAM_CUSTOM_SCRIPT "apps\uber\Uber.cs" ponteiro ponteiro_label este_app
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
    WHILE var = este_app
        READ_MEMORY ponteiro 4 FALSE var
        WHILE var = este_app
        AND IS_CHAR_PLAYING_ANIM scplayer "phone_grab"
            READ_MEMORY ponteiro 4 FALSE var
            WAIT 0
        ENDWHILE
        IF IS_CHAR_PLAYING_ANIM scplayer "phone_grab"
            WRITE_MEMORY ponteiro 4 -1 FALSE
        ELSE
            WRITE_MEMORY ponteiro 4 -2 FALSE
        ENDIF
        WAIT 0
    ENDWHILE
GOTO pegar_celular
}
SCRIPT_END