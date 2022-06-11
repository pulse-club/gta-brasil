SCRIPT_START
{
LVAR_INT scplayer
LVAR_FLOAT pos_x pos_y tam_x tam_y
LVAR_INT selecionado alpha2 alpha3 var quantidade obj braba ponteiro_label desativar_setas desativar_botões h
LVAR_FLOAT sel_x sel_y icone_x icone_y alpha1 saude

LOAD_TEXTURE_DICTIONARY CELULAR

LOAD_SPRITE 1 "moto_g"
LOAD_SPRITE 2 "fundo"
LOAD_SPRITE 3 "nav_bar"
LOAD_SPRITE 4 "moto_d"
IF NOT scplayer = 0
    braba = -2
    GOTO aparelho
ENDIF

GET_PLAYER_CHAR 0 scplayer

CONST_INT icones 0
CONST_INT lista 1

pos_x = 555.5
pos_y = 345.0
sel_x = 560.0
sel_y = 355.0
alpha1 = 180.0

CLEO_CALL escrever_tudo 0 (desativar_setas)
GET_LABEL_POINTER infos ponteiro_label

ponteiro_label += 32
READ_MEMORY ponteiro_labeL 2 FALSE desativar_botões
ponteiro_label -= 32

//abrir apps
GET_VAR_POINTER braba var
CLEO_CALL chamar 0 var ponteiro_label pos_x pos_y (quantidade)

braba = -2

GET_FIXED_XY_ASPECT_RATIO (110.0 220.0) (tam_x tam_y)
GET_FIXED_XY_ASPECT_RATIO (27.0 25.0) (icone_x icone_y)

//~r~  ~g~



STREAM_CUSTOM_SCRIPT "Celular (nadalao).cs" var pos_x pos_y tam_x tam_y//

REQUEST_ANIMATION "PHONE"
REQUEST_MODEL 330
LOAD_ALL_MODELS_NOW

main_loop:
    WAIT 0 
    IF NOT IS_CHAR_PLAYING_ANIM scplayer "phone_grab"
        SET_PLAYER_CYCLE_WEAPON_BUTTON 0 TRUE
        braba = -2
        IF DOES_OBJECT_EXIST obj
            DELETE_OBJECT obj
            GET_LABEL_POINTER infos ponteiro_label
            WRITE_MEMORY ponteiro_label 2 0 FALSE
        ENDIF
        //esperando pegar o celular
        WHILE TRUE
            WAIT 0
            IF CLEO_CALL comandos 0 (3)//pegar
                WHILE CLEO_CALL comandos 0 (3)//pegar
                    WAIT 0
                ENDWHILE
                BREAK
            ENDIF
        ENDWHILE
        IF NOT IS_PLAYER_CONTROL_ON 0
        OR IS_CHAR_SWIMMING scplayer
            GOTO main_loop
        ENDIF
        //no_menu
        braba = -1
        IF NOT DOES_OBJECT_EXIST obj
            CREATE_OBJECT 330 0.0 0.0 0.0 obj
            CLEO_CALL DontSaveObject 0 obj
            MARK_OBJECT_AS_NO_LONGER_NEEDED obj
            GET_LABEL_POINTER infos ponteiro_label
            WRITE_MEMORY ponteiro_label 2 obj FALSE
        ENDIF
        TASK_PICK_UP_OBJECT scplayer obj 0.03 0.0 0.0 6 16 "phone_grab" "PHONE" -1
        IF IS_CHAR_IN_ANY_CAR scplayer
            TASK_PLAY_ANIM scplayer "phone_grab" "PHONE" 4.0 1 0 0 0 -1
        ENDIF
        GOTO main_loop
    ENDIF
    IF braba = -1
        GOSUB direções
        SET_CURRENT_CHAR_WEAPON scplayer 0
        SET_PLAYER_CYCLE_WEAPON_BUTTON 0 FALSE
        CLEO_CALL SimulateKey 0 17 //atacar
        IF CLEO_CALL comandos 0 (1)//confirmar
            WHILE CLEO_CALL comandos 0 (1)//confirmar
                GOSUB desenhar
                WAIT 0
            ENDWHILE
            braba = selecionado
            app_aberto:
            IF braba >= 0
                WHILE braba >= 0
                    CLEO_CALL SimulateKey 0 14 //pulo
                    CLEO_CALL SimulateKey 0 17 //atacar
                    GOSUB desenhar
                    WAIT 0
                ENDWHILE
            ENDIF
            IF braba = -3
                WHILE braba = -3
                    WAIT 0
                ENDWHILE
                GOTO app_aberto
            ENDIF
        ENDIF
        IF CLEO_CALL comandos 0 (2)//voltar
            WHILE CLEO_CALL comandos 0 (2)//voltar
                CLEO_CALL SimulateKey 0 14 //pulo
                GOSUB desenhar
                WAIT 0
            ENDWHILE
            braba = -2
            IF DOES_OBJECT_EXIST obj
                DELETE_OBJECT obj
                GET_LABEL_POINTER infos ponteiro_label
                WRITE_MEMORY ponteiro_label 2 0 FALSE
            ENDIF
            IF IS_CHAR_IN_ANY_CAR scplayer
                CLEAR_CHAR_TASKS scplayer
                TASK_PLAY_ANIM scplayer "NULL" "NULL" 4.0 0 0 0 0 -1
            ELSE
                CLEO_CALL ClearSecondaryTask 0 (scplayer)
            ENDIF
        ENDIF
    ENDIF

    GOSUB desenhar
GOTO main_loop

aparelho:
    READ_MEMORY scplayer 4 FALSE braba
    IF braba > -2
        USE_TEXT_COMMANDS 1

        GET_PLAYER_CHAR 0 h
        GET_CHAR_HEALTH_PERCENT h saude
        IF saude < 50.0
            DRAW_SPRITE 4 pos_x pos_y tam_x tam_y 225 225 225 255
        ELSE
            DRAW_SPRITE 1 pos_x pos_y tam_x tam_y 225 225 225 255
        ENDIF

        USE_TEXT_COMMANDS 0
        IF braba > -2
            timera = 0
        ENDIF
    ENDIF
    WAIT 0
GOTO aparelho

desenhar:
    IF desativar_setas = 1
        CLEO_CALL desativar_setas 0
    ENDIF
    IF desativar_botões = 1
        CLEO_CALL desativar_botões 0 (1)//CONFIRMAR
        CLEO_CALL desativar_botões 0 (2)//CANCELAR
    ENDIF
    //fundo
    USE_TEXT_COMMANDS 1
    tam_x -= 9.5
    tam_y -= 10.0
    pos_x += 0.25
    DRAW_SPRITE 2 pos_x pos_y tam_x tam_y 225 225 225 255
    pos_x -= 0.25
    tam_x += 9.5
    tam_y += 10.0
    //nav bar
    tam_x -= 5.0
    tam_y -= 190.0
    pos_y += 85.0
    DRAW_SPRITE 3 pos_x pos_y tam_x tam_y 225 225 225 255
    tam_x += 5.0
    tam_y += 190.0
    pos_y -= 85.0
 
    CLEO_CALL retornar_selecionado 0 selecionado pos_x pos_y (sel_x sel_y)
    IF braba = -1
        DRAW_RECT sel_x sel_y icone_x icone_y 5 5 20 alpha2//seleção
    ENDIF
    CLEO_CALL horas 0 pos_x pos_y

    //celular
    GET_PLAYER_CHAR 0 h
    GET_CHAR_HEALTH_PERCENT h saude
    IF saude < 50.0
        DRAW_SPRITE 4 pos_x pos_y tam_x tam_y 225 225 225 255
    ELSE
        DRAW_SPRITE 1 pos_x pos_y tam_x tam_y 225 225 225 255
    ENDIF
    //transparencia da seleção
    IF var = 0
        alpha1 -=@ 1.0
    ELSE
        alpha1 +=@ 1.0
    ENDIF
    IF alpha1 > 150.0
        VAR = 0
    ENDIF
    IF alpha1 < 100.0
        var = 1
    ENDIF
    alpha2 =# alpha1
    USE_TEXT_COMMANDS 0
RETURN

direções:
    IF CLEO_CALL comandos 0 (4)//cima
        selecionado -= 3
        IF selecionado < 0
        OR selecionado > quantidade
            selecionado += 3
        ENDIF
        WHILE CLEO_CALL comandos 0 (4)//cima
            GOSUB desenhar
            WAIT 0
        ENDWHILE
    ENDIF
    IF CLEO_CALL comandos 0 (5)//baixo
        selecionado += 3
        IF selecionado > 17
        OR selecionado > quantidade
            selecionado -= 3
        ENDIF
        WHILE CLEO_CALL comandos 0 (5)//baixo
            GOSUB desenhar
            WAIT 0
        ENDWHILE
    ENDIF
    IF CLEO_CALL comandos 0 (6)//esquerda
        IF NOT selecionado = 0
        AND NOT selecionado = 3
        AND NOT selecionado = 6
        AND NOT selecionado = 9
        AND NOT selecionado = 12
        AND NOT selecionado = 15
            selecionado -= 1
            IF selecionado < 0
            OR selecionado > quantidade
                selecionado += 1
            ENDIF
            WHILE CLEO_CALL comandos 0 (6)//esquerda
                GOSUB desenhar
                WAIT 0
            ENDWHILE
        ENDIF
    ENDIF
    IF CLEO_CALL comandos 0 (7)//direita
        IF NOT selecionado = 2
        AND NOT selecionado = 5
        AND NOT selecionado = 8
        AND NOT selecionado = 11
        AND NOT selecionado = 14
        AND NOT selecionado = 17
            selecionado += 1
            IF selecionado > 17
            OR selecionado > quantidade
                selecionado -= 1
            ENDIF
            WHILE CLEO_CALL comandos 0 (7)//direita
                GOSUB desenhar
                WAIT 0
            ENDWHILE
        ENDIF
    ENDIF
RETURN

}

{   
    LVAR_INT ponteiro_label valor offset slot desativar_setas desativar_botões
    LVAR_FLOAT valor_float
    escrever_tudo:

        GET_LABEL_POINTER infos ponteiro_label

        slot = 1

        //Keyboad/Mouse
        IF NOT READ_INT_FROM_INI_FILE "cleo\Celular.ini" "Keyboad/Mouse" "Accept" valor
            valor = 161//VK_RSHIFT    CONFIRMAR
        ENDIF
        GOSUB escrever_int
        IF NOT READ_INT_FROM_INI_FILE "cleo\Celular.ini" "Keyboad/Mouse" "Back" valor
            valor = 163//VK_RSHIFT    VOLTAR
        ENDIF
        GOSUB escrever_int
        IF NOT READ_INT_FROM_INI_FILE "cleo\Celular.ini" "Keyboad/Mouse" "Draw" valor
            valor = 161//VK_RSHIFT    PEGAR
        ENDIF
        GOSUB escrever_int
        IF NOT READ_INT_FROM_INI_FILE "cleo\Celular.ini" "Keyboad/Mouse" "Up" valor
            valor = 38//VK_UP         CIMA
        ENDIF
        GOSUB escrever_int
        IF NOT READ_INT_FROM_INI_FILE "cleo\Celular.ini" "Keyboad/Mouse" "Down" valor
            valor = 40//VK_DOWN       BAIXO
        ENDIF
        GOSUB escrever_int
        IF NOT READ_INT_FROM_INI_FILE "cleo\Celular.ini" "Keyboad/Mouse" "Left" valor
            valor = 37//VK_LEFT       ESQUERDA
        ENDIF
        GOSUB escrever_int
        IF NOT READ_INT_FROM_INI_FILE "cleo\Celular.ini" "Keyboad/Mouse" "Right" valor
            valor = 39//VK_RIGHT      DIREITA
        ENDIF
        GOSUB escrever_int

        //Controller
        IF NOT READ_INT_FROM_INI_FILE "cleo\Celular.ini" "Controller" "Accept" valor
            valor = 4//L1
        ENDIF
        GOSUB escrever_int
        IF NOT READ_INT_FROM_INI_FILE "cleo\Celular.ini" "Controller" "Back" valor
            valor = 6//R1
        ENDIF
        GOSUB escrever_int
        IF NOT READ_INT_FROM_INI_FILE "cleo\Celular.ini" "Controller" "Draw" valor
            valor = 10//ESQUERDA
        ENDIF
        GOSUB escrever_int
        IF NOT READ_INT_FROM_INI_FILE "cleo\Celular.ini" "Controller" "Up" valor
            valor = 8//CIMA
        ENDIF
        GOSUB escrever_int
        IF NOT READ_INT_FROM_INI_FILE "cleo\Celular.ini" "Controller" "Down" valor
            valor = 9//BAIXO
        ENDIF
        GOSUB escrever_int
        IF NOT READ_INT_FROM_INI_FILE "cleo\Celular.ini" "Controller" "Left" valor
            valor = 10//ESQUERDA
        ENDIF
        GOSUB escrever_int
        IF NOT READ_INT_FROM_INI_FILE "cleo\Celular.ini" "Controller" "Right" valor
            valor = 11//DIREITA
        ENDIF
        GOSUB escrever_int

        //General
        IF NOT READ_INT_FROM_INI_FILE "cleo\Celular.ini" "General" "Disable Arrows" desativar_setas
            valor = 1
        ELSE
            valor = desativar_setas
        ENDIF
        GOSUB escrever_int
        IF NOT READ_INT_FROM_INI_FILE "cleo\Celular.ini" "General" "Disable Buttons" desativar_botões
            valor = 1
        ELSE
            valor = desativar_botões
        ENDIF
        GOSUB escrever_int

        //slot = 16 era 14
        //offset = 32 era 28
        //posição
        slot = 0
        valor_float = 555.5
        GOSUB escrever_float//posição x
        valor_float = 335.5
        GOSUB escrever_float//posição y
        valor_float = 97.0
        GOSUB escrever_float//tamanho x
        valor_float = 186.1
        GOSUB escrever_float//tamanho y

    CLEO_RETURN 0 desativar_setas

    escrever_int:
        GET_LABEL_POINTER infos ponteiro_label
        offset = slot * 2
        ponteiro_label += offset
        WRITE_MEMORY ponteiro_label 2 valor FALSE
        slot ++
    RETURN
    
    escrever_float:
        GET_LABEL_POINTER infos ponteiro_label
        offset = slot * 4
        offset += 34
        ponteiro_label += offset
        WRITE_MEMORY ponteiro_label 4 valor_float FALSE
        slot ++
    RETURN
}
{
    LVAR_INT adress valor
    desativar_setas:
    IF IS_KEY_PRESSED VK_UP
    AND NOT IS_KEY_PRESSED VK_KEY_W
        IF IS_KEY_PRESSED VK_KEY_S
            valor = 128
        ENDIF
        adress = 1
        GOSUB SimulateButton
    ENDIF
    IF IS_KEY_PRESSED VK_DOWN
    AND NOT IS_KEY_PRESSED VK_KEY_S
        IF IS_KEY_PRESSED VK_KEY_W
            valor = -128
        ENDIF
        adress = 1
        GOSUB SimulateButton
    ENDIF
    IF IS_KEY_PRESSED VK_LEFT
    AND NOT IS_KEY_PRESSED VK_KEY_A
        IF IS_KEY_PRESSED VK_KEY_D
            valor = 128
        ENDIF
        adress = 0
        GOSUB SimulateButton
    ENDIF
    IF IS_KEY_PRESSED VK_RIGHT
    AND NOT IS_KEY_PRESSED VK_KEY_D
        IF IS_KEY_PRESSED VK_KEY_A
            valor = -128
        ENDIF
        adress = 0
        GOSUB SimulateButton
    ENDIF
    IF IS_KEY_PRESSED VK_KEY_W
        IF IS_KEY_PRESSED VK_DOWN
            IF NOT IS_KEY_PRESSED VK_KEY_A
                IF NOT IS_KEY_PRESSED VK_KEY_D
                    IF IS_KEY_PRESSED VK_LEFT
                    OR IS_KEY_PRESSED VK_RIGHT
                        adress = 0
                        VALOR = 0
                        GOSUB SimulateButton
                    ENDIF
                ENDIF
            ENDIF
        ENDIF
    ENDIF
    IF IS_KEY_PRESSED VK_KEY_S
        IF IS_KEY_PRESSED VK_UP
            IF NOT IS_KEY_PRESSED VK_KEY_A
                IF NOT IS_KEY_PRESSED VK_KEY_D
                    IF IS_KEY_PRESSED VK_LEFT
                    OR IS_KEY_PRESSED VK_RIGHT
                        adress = 0
                        VALOR = 0
                        GOSUB SimulateButton
                    ENDIF
                ENDIF
            ENDIF
        ENDIF
    ENDIF

    CLEO_RETURN 0
    SimulateButton:
        adress *= 2
        adress += 0xB73458 
        WRITE_MEMORY adress 2 valor FALSE
    RETURN
}
{
SimulateKey:
    LVAR_INT adress
    adress *= 2
    adress += 0xB73458 
    WRITE_MEMORY adress 2 0 0
CLEO_RETURN 0
}
{
    LVAR_INT hObj //In
    LVAR_INT pObj
    DontSaveObject:
    GET_OBJECT_POINTER hObj pObj
    pObj += 0x13C //m_nObjectType
    WRITE_MEMORY pObj 1 6 FALSE
    CLEO_RETURN 0
}
{
    LVAR_FLOAT pos_x pos_y 
    LVAR_INT horas minutos var[3]
    horas:
        pos_y -= 92.0
        DRAW_RECT pos_x pos_y 71.7 10.0 0 0 0 100
        pos_y += 92.0
        GET_TIME_OF_DAY horas minutos
        GET_LABEL_POINTER buffer3 var[0]
        GET_LABEL_POINTER buffer4 var[1]
        GET_LABEL_POINTER buffer5 var[2]
        STRING_FORMAT var[0] "%02d" horas
        STRING_FORMAT var[1] "%02d" minutos
        STRING_FORMAT var[2] "%s:%s" $var[0] $var[1]
        ADD_TEXT_LABEL (TEMPGXT) $var[2]
        SET_TEXT_SCALE 0.2 0.9
        SET_TEXT_CENTRE_SIZE 640.0
        SET_TEXT_EDGE 1 (0 0 0 255)
        pos_x -= 28.5 
        pos_y -= 96.0
        DISPLAY_TEXT (pos_x pos_y) TEMPGXT
    CLEO_RETURN 0
}
{
    LVAR_INT selecionado
    LVAR_FLOAT pos_x pos_y sel_x sel_y
    retornar_selecionado:
        SWITCH selecionado
            CASE 0
                sel_x = pos_x - 23.0
                sel_y = pos_y - 66.0
                BREAK
            CASE 1
                sel_x = pos_x
                sel_y = pos_y - 66.0
                BREAK
            CASE 2
                sel_x = pos_x + 23.0
                sel_y = pos_y - 66.0
                BREAK
            CASE 3
                sel_x = pos_x - 23.0
                sel_y = pos_y - 41.0
                BREAK
            CASE 4
                sel_x = pos_x
                sel_y = pos_y - 41.0
                BREAK
            CASE 5
                sel_x = pos_x + 23.0
                sel_y = pos_y - 41.0
                BREAK
            CASE 6
                sel_x = pos_x - 23.0
                sel_y = pos_y - 16.0
                BREAK
            CASE 7
                sel_x = pos_x
                sel_y = pos_y - 16.0
                BREAK
            CASE 8
                sel_x = pos_x + 23.0
                sel_y = pos_y - 16.0
                BREAK
            CASE 9
                sel_x = pos_x - 23.0
                sel_y = pos_y + 9.0
                BREAK
            CASE 10
                sel_x = pos_x
                sel_y = pos_y + 9.0
                BREAK
            CASE 11
                sel_x = pos_x + 23.0
                sel_y = pos_y + 9.0
                BREAK
            CASE 12
                sel_x = pos_x - 23.0
                sel_y = pos_y + 34.0
                BREAK
            CASE 13
                sel_x = pos_x
                sel_y = pos_y + 34.0
                BREAK
            CASE 14
                sel_x = pos_x + 23.0
                sel_y = pos_y + 34.0
                BREAK
            CASE 15
                sel_x = pos_x - 23.0
                sel_y = pos_y + 59.0
                BREAK
            CASE 16
                sel_x = pos_x
                sel_y = pos_y + 59.0
                BREAK
            CASE 17
                sel_x = pos_x + 23.0
                sel_y = pos_y + 59.0
                BREAK
        ENDSWITCH
    CLEO_RETURN 0 sel_x sel_y
}
{
    chamar:
    LVAR_INT ponteiro ponteiro_label
    LVAR_FLOAT pos_x pos_y sel_x sel_y
    LVAR_INT name find id i var

    GET_LABEL_POINTER buffer name
    IF FIND_FIRST_FILE "modloader\GTA Brasil - Opcional\GTABR-Celular Beta\cleo\apps\*.cs" find name
        WHILE TRUE
            WAIT 0
            READ_MEMORY name 1 FALSE (i)
            IF i = 0x2E // '.'
                id --
                BREAK
            ENDIF
            CLEO_CALL retornar_selecionado 0 id pos_x pos_y (sel_x sel_y)
            GET_LABEL_POINTER buffer2 var
            STRING_FORMAT var "apps\%s" $name
            IF STREAM_CUSTOM_SCRIPT $var ponteiro ponteiro_label id sel_x sel_y
                id += 1
            ENDIF
            IF NOT FIND_NEXT_FILE find name
                id --
                BREAK
            ENDIF
        ENDWHILE
    ENDIF
    CLEO_RETURN 0 id
}
{
    //CLEO_CALL ClearSecondaryTask 0 (scplayer)
    LVAR_INT hChar // in
    LVAR_INT p
    ClearSecondaryTask:
    GET_PED_POINTER hChar p
    p += 0x47C //m_pIntelligence
    READ_MEMORY p 4 FALSE (p) 
    CALL_METHOD 0x601420 p 2 0 (1 0)
    CLEO_RETURN 0
}
{
    LVAR_INT comando ponteiro_label
    LVAR_INT botão desativar_botões
    desativar_botões:
    GET_LABEL_POINTER infos ponteiro_label
    comando *= 2
    ponteiro_label += comando
    ponteiro_label += 14
    READ_MEMORY ponteiro_label 2 FALSE botão
    
    CLEO_CALL SimulateKey 0 botão

    CLEO_RETURN 0
}
{
    LVAR_INT comando ponteiro_label
    LVAR_INT botão[2] desativar_botões
    comandos:
    GET_LABEL_POINTER infos ponteiro_label
    comando *= 2
    ponteiro_label += comando
    READ_MEMORY ponteiro_label 2 FALSE botão[0]
    ponteiro_label += 14
    READ_MEMORY ponteiro_label 2 FALSE botão[1]
    comando /= 2
    
    IF IS_PC_USING_JOYPAD
        IF IS_BUTTON_PRESSED 0 botão[1]//LB
            RETURN_TRUE
        ELSE
            RETURN_FALSE
        ENDIF
    ELSE
        IF IS_KEY_PRESSED botão[0]//write_memory
            RETURN_TRUE
        ELSE
            RETURN_FALSE
        ENDIF
    ENDIF

    CLEO_RETURN 0
}

buffer5:
DUMP
00 00 00 00 00 00 00 00
00 00 00 00 00 00 00 00
00 00 00 00 00 00 00 00
00 00 00 00 00 00 00 00
00 00 00 00 00 00 00 00
00 00 00 00 00 00 00 00
00 00 00 00 00 00 00 00
00 00 00 00 00 00 00 00
00 00 00 00 00 00 00 00
ENDDUMP

buffer4:
DUMP
00 00 00 00 00 00 00 00
00 00 00 00 00 00 00 00
00 00 00 00 00 00 00 00
00 00 00 00 00 00 00 00
00 00 00 00 00 00 00 00
00 00 00 00 00 00 00 00
00 00 00 00 00 00 00 00
00 00 00 00 00 00 00 00
00 00 00 00 00 00 00 00
ENDDUMP

buffer3:
DUMP
00 00 00 00 00 00 00 00
00 00 00 00 00 00 00 00
00 00 00 00 00 00 00 00
00 00 00 00 00 00 00 00
00 00 00 00 00 00 00 00
00 00 00 00 00 00 00 00
00 00 00 00 00 00 00 00
00 00 00 00 00 00 00 00
00 00 00 00 00 00 00 00
ENDDUMP

buffer2:
DUMP
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
ENDDUMP

buffer:
DUMP
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
00 00 00 00
ENDDUMP

infos:
DUMP
00 00//0 objeto celular

//Keyboad/Mouse
00 00//1 confirmar
00 00//2 voltar
00 00//3 pegar
00 00//4 cima
00 00//5 baixo
00 00//6 esquerda
00 00//7 dierita

//Controller
00 00//8 confirmar
00 00//9 voltar
00 00//10 pegar
00 00//11 cima
00 00//12 baixo
00 00//13 esquerda
00 00//14 direita

//General
00 00//15 desativar setas
00 00//16 desativar botões

00 00 00 00//17 posição x
00 00 00 00//18 posição y
00 00 00 00//19 tamanho x
00 00 00 00//20 tamanho y
00
ENDDUMP

SCRIPT_END