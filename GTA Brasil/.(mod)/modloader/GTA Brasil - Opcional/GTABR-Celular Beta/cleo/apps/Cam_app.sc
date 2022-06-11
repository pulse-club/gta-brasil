SCRIPT_START
{
LVAR_INT ponteiro ponteiro_label este_app
LVAR_FLOAT pos_x pos_y tam_x tam_y angulo_x angulo_y angulo_z mão_x mão_z x y z XA XC
LVAR_INT scplayer var LR UD modo obj lixo 

IF ponteiro = 0
    TERMINATE_THIS_CUSTOM_SCRIPT
ENDIF

GET_PLAYER_CHAR 0 scplayer

CLEO_CALL GetXYSizeInScreen4x3ScaleBy640x480 0 (20.0 20.0) (tam_x tam_y)

//esconder da tela  =-3
//não está sendo usado -2
//no menu do celular -1

CONST_INT traseira 1
CONST_INT frontal 0

angulo_x = 0.08
angulo_y = 0.6
mão_z = 1.0

LOAD_TEXTURE_DICTIONARY camera
LOAD_SPRITE 1 "camera"

//modo = traseira

pegar_celular:

    WHILE NOT var = -1
        READ_MEMORY ponteiro 4 FALSE var
        WAIT 0
    ENDWHILE

    //menu principal
    WHILE var = -1
        READ_MEMORY ponteiro 4 FALSE var
        USE_TEXT_COMMANDS 1
        DRAW_SPRITE 1 pos_x pos_y tam_x tam_y 225 225 225 255
        USE_TEXT_COMMANDS 0
        WAIT 0
    ENDWHILE

    IF NOT var = este_app
        READ_MEMORY ponteiro 4 FALSE var
        GOTO pegar_celular
    ENDIF

    IF IS_CHAR_IN_ANY_CAR scplayer
        WRITE_MEMORY ponteiro 4 -1 FALSE
        GOTO pegar_celular
    ENDIF

    app_aberto:
    WHILE var = este_app
        READ_MEMORY ponteiro 4 FALSE var
        GOSUB camera
        WAIT 0
    ENDWHILE
GOTO pegar_celular

camera:
    IF NOT HAS_ANIMATION_LOADED "camer"
        REQUEST_ANIMATION "camer"
        WHILE NOT HAS_ANIMATION_LOADED "camer"
            WAIT 0
        ENDWHILE
    ENDIF
    TASK_PLAY_ANIM_SECONDARY scplayer "segurar" "camer" 4.0 1 1 1 1 -1
    WRITE_MEMORY ponteiro 4 -3 FALSE //esconder
    IF NOT IS_CHAR_PLAYING_ANIM scplayer "segurar"
        WAIT 0
        GOTO camera
    ENDIF
    READ_MEMORY ponteiro_label 2 FALSE obj
    IF DOES_OBJECT_EXIST obj
        DELETE_OBJECT obj
    ENDIF
    DISPLAY_HUD FALSE
    DISPLAY_RADAR FALSE
    IF modo = frontal
        IF IS_PC_USING_JOYPAD 
            PRINT_HELP_FOREVER CAMAPP3
        ELSE
            PRINT_HELP_FOREVER CAMAPP1
        ENDIF
    ELSE
        IF IS_PC_USING_JOYPAD 
            PRINT_HELP_FOREVER CAMAPP4
        ELSE
            PRINT_HELP_FOREVER CAMAPP2
        ENDIF
    ENDIF
    WHILE IS_CHAR_PLAYING_ANIM scplayer "segurar"
        READ_MEMORY ponteiro 4 FALSE var
        GET_POSITION_OF_ANALOGUE_STICKS 0 timera timera LR UD
        GET_PC_MOUSE_MOVEMENT XA XC
        IF GOSUB trocar_modo
            IF modo = frontal
                modo = traseira
                IF IS_PC_USING_JOYPAD 
                    PRINT_HELP_FOREVER CAMAPP3
                ELSE
                    PRINT_HELP_FOREVER CAMAPP1
                ENDIF
                WRITE_MEMORY 0xB6F059 1 0 FALSE
            ELSE
                modo = frontal
                IF IS_PC_USING_JOYPAD 
                    PRINT_HELP_FOREVER CAMAPP4
                ELSE
                    PRINT_HELP_FOREVER CAMAPP2
                ENDIF
                SET_NEAR_CLIP 0.05
            ENDIF
            WHILE NOT GOSUB trocar_modo
                CLEO_CALL desativar_comandos 0
                WAIT 0
            ENDWHILE
            angulo_x = 0.08
            angulo_y = 0.6
            mão_z = 1.0
        ENDIF
        IF GOSUB tirar_foto
            TAKE_PHOTO TRUE
            ADD_ONE_OFF_SOUND 0.0 0.0 0.0 1132
            WHILE NOT GOSUB tirar_foto
                CLEO_CALL desativar_comandos 0
                WAIT 0
            ENDWHILE
        ENDIF
        IF modo = frontal
            XA /= 600.0
            XC /= 1300.0
            IF XA > 12.0
                XA = 12.0
            ENDIF
            IF XA < -12.0
                XA = -12.0
            ENDIF
            IF XC > 2.0
                XC = 2.0
            ENDIF
            IF XC < -2.0
                XC = -2.0
            ENDIF
            angulo_x -=@ XA
            angulo_y +=@ XC
            //analógico
            IF lr < -10
                angulo_x +=@ 0.015
            ENDIF
            IF lr > 10
                angulo_x -=@ 0.015
            ENDIF
            IF ud < -10
                angulo_y +=@ 0.015
            ENDIF
            IF ud > 10
                angulo_y -=@ 0.015
            ENDIF
            //limites
            IF angulo_x < -0.06
                angulo_x = -0.06
                GET_CHAR_HEADING scplayer angulo_z
                angulo_z -=@ 0.85
                SET_CHAR_HEADING scplayer angulo_z
            ENDIF
            IF angulo_x > 0.7
                angulo_x = 0.7
                GET_CHAR_HEADING scplayer angulo_z
                angulo_z +=@ 0.85
                SET_CHAR_HEADING scplayer angulo_z
            ENDIF
            IF angulo_y < -0.1
                angulo_y = -0.1
            ENDIF
            IF angulo_y > 1.0
                angulo_y = 1.0
            ENDIF
            IF angulo_x < 0.0
                mão_x -= angulo_x
            ENDIF
            ATTACH_CAMERA_TO_CHAR scplayer mão_x 0.8 0.9 angulo_x 0.0 angulo_y 0.0 2
            IF angulo_x < 0.0
                mão_x += angulo_x
            ENDIF
        ELSE
            GET_CHAR_HEADING scplayer angulo_x
            XA /= 12.0
            XC /= 85.0
            IF XA > 12.0
                XA = 12.0
            ENDIF
            IF XA < -12.0
                XA = -12.0
            ENDIF
            IF XC > 2.0
                XC = 2.0
            ENDIF
            IF XC < -2.0
                XC = -2.0
            ENDIF
            angulo_x -=@ XA
            angulo_y +=@ XC
            //analógico
            IF lr < -10
                angulo_x +=@ 1.2
            ENDIF
            IF lr > 10
                angulo_x -=@ 1.2
            ENDIF
            IF ud < -10
                angulo_y +=@ 0.08
            ENDIF
            IF ud > 10
                angulo_y -=@ 0.08
            ENDIF
            GET_OFFSET_FROM_CHAR_IN_WORLD_COORDS scplayer 0.0 0.5 0.6 x y z
            SET_FIXED_CAMERA_POSITION x y z 0.0 0.0 0.0
            SET_CHAR_HEADING scplayer angulo_x
            GET_OFFSET_FROM_CHAR_IN_WORLD_COORDS scplayer 0.0 5.0 angulo_y x y z
            POINT_CAMERA_AT_POINT x y z 2
        ENDIF
        IF CLEO_CALL comandos 0 (2) ponteiro_label
            WHILE CLEO_CALL comandos 0 (2) ponteiro_label
                CLEO_CALL desativar_comandos 0
                WAIT 0
            ENDWHILE
            READ_MEMORY ponteiro_label 2 FALSE obj
            IF DOES_OBJECT_EXIST obj
                TASK_PICK_UP_OBJECT scplayer obj 0.03 0.0 0.0 6 16 "phone_grab" "PHONE" -1
                IF IS_CHAR_IN_ANY_CAR scplayer
                    TASK_PLAY_ANIM scplayer "phone_grab" "PHONE" 4.0 1 0 0 0 -1
                ENDIF
            ENDIF
            BREAK
        ENDIF
        SET_PLAYER_DRUNKENNESS 0 3
        CLEO_CALL desativar_comandos 0
        WAIT 0
    ENDWHILE

    SET_PLAYER_DRUNKENNESS 0 0
        
    READ_MEMORY ponteiro_label 2 FALSE obj
    IF NOT DOES_OBJECT_EXIST obj
        CREATE_OBJECT 330 0.0 0.0 0.0 obj
        CLEO_CALL DontSaveObject 0 obj
        TASK_PICK_UP_OBJECT scplayer obj 0.03 0.0 0.0 6 16 "phone_grab" "PHONE" -1
        WRITE_MEMORY ponteiro_label 2 obj FALSE
        MARK_OBJECT_AS_NO_LONGER_NEEDED obj
    ENDIF

    WRITE_MEMORY ponteiro 4 -1 FALSE
    READ_MEMORY ponteiro 4 FALSE var
    WRITE_MEMORY 0xB6F059 1 0 FALSE //tirar near_clip
    CLEAR_HELP

    RESTORE_CAMERA_JUMPCUT
    DISPLAY_HUD TRUE
    DISPLAY_RADAR TRUE
RETURN

trocar_modo:
    IF IS_PC_USING_JOYPAD
        IF IS_BUTTON_PRESSED 0 14//PULAR
            RETURN_TRUE
        ELSE
            RETURN_FALSE
        ENDIF
    ELSE
        IF IS_BUTTON_PRESSED 0 6//MIRAR
            RETURN_TRUE
        ELSE
            RETURN_FALSE
        ENDIF
    ENDIF
    RETURN

tirar_foto:
    IF IS_PC_USING_JOYPAD
        IF IS_BUTTON_PRESSED 0 6//RB
            RETURN_TRUE
        ELSE
            RETURN_FALSE
        ENDIF
    ELSE
        IF IS_BUTTON_PRESSED 0 17//atirar
            RETURN_TRUE
        ELSE
            RETURN_FALSE
        ENDIF
    ENDIF
    RETURN

cancelar:
    IF IS_PC_USING_JOYPAD
        IF IS_BUTTON_PRESSED 0 4//LB
            CLEO_CALL SimulateKey 0 (6)
            RETURN_TRUE
        ELSE
            RETURN_FALSE
        ENDIF
    ELSE
        IF IS_KEY_PRESSED VK_RCONTROL
            RETURN_TRUE
        ELSE
            RETURN_FALSE
        ENDIF
    ENDIF
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
{// by fastman92
    LVAR_FLOAT x y // In
    LVAR_FLOAT fresX fresY
    LVAR_INT iresX iresY

    GetXYSizeInScreen4x3ScaleBy640x480:
    READ_MEMORY 0x00C17044 4 FALSE (iresX) // Get current resolution X

    READ_MEMORY 0x00C17048 4 FALSE (iresY) // Y
    fresX =# iresX
    fresY =# iresY

    fresY *= 1.33333333
    fresX /= fresY
    x /= fresX

    y /= 1.07142857
    CLEO_RETURN 0 (x y)
}
{
    LVAR_INT endereço var
    desativar_comandos:
    endereço = var
    endereço *= 2
    endereço += 0xB73458 
    WRITE_MEMORY endereço 2 0 0
    var ++
    IF var < 20
        GOTO desativar_comandos
    ENDIF
    CLEO_RETURN 0
}
{
    LVAR_INT comando ponteiro_label
    LVAR_INT botão[2]
    comandos:
    comando *= 2
    ponteiro_label += comando
    READ_MEMORY ponteiro_label 2 FALSE botão[0]
    ponteiro_label += 14
    READ_MEMORY ponteiro_label 2 FALSE botão[1]
    comando /= 2
    IF IS_PC_USING_JOYPAD
        IF IS_BUTTON_PRESSED 0 botão[1]//LB
            IF comando = 2
                CLEO_CALL SimulateKey 0 comando
            ENDIF
            RETURN_TRUE
        ELSE
            RETURN_FALSE
        ENDIF
    ELSE
        IF IS_KEY_PRESSED botão[0]
            RETURN_TRUE
        ELSE
            RETURN_FALSE
        ENDIF
    ENDIF
CLEO_RETURN 0
}
SCRIPT_END