SCRIPT_START
{
LVAR_INT ponteiro ponteiro_label este_app
LVAR_FLOAT pos_x pos_y

LVAR_INT scplayer luz obj marca var ol idist idist2 var2
LVAR_FLOAT x[3] y[3] z[4] angulo dist

GET_PLAYER_CHAR 0 scplayer

inicio:
WHILE NOT var2 = este_app
    READ_MEMORY ponteiro 4 FALSE var2
    WAIT 0
ENDWHILE

WHILE TRUE
    READ_MEMORY ponteiro 4 FALSE var2
    IF GOSUB check
        GOTO inicio
    ENDIF
    IF CLEO_CALL comandos 0 ponteiro_label (2)//VOLTAR
        WHILE CLEO_CALL comandos 0 ponteiro_label (2)//VOLTAR
            WAIT 0
        ENDWHILE
        WRITE_MEMORY ponteiro 4 -1 FALSE
        GOTO inicio
    ENDIF
    IF NOT IS_CHAR_IN_AIR scplayer
    AND var = 0
        GOSUB searchlight
        WAIT 0
        IF DOES_SEARCHLIGHT_EXIST luz
            DELETE_SEARCHLIGHT luz
        ENDIF
    ELSE
        IF DOES_SEARCHLIGHT_EXIST luz
            DELETE_SEARCHLIGHT luz
        ENDIF
        WHILE IS_CHAR_IN_AIR scplayer
            WAIT 0
            IF DOES_SEARCHLIGHT_EXIST luz
                DELETE_SEARCHLIGHT luz
            ENDIF
        ENDWHILE
        WAIT 0
    ENDIF
ENDWHILE

check:
READ_MEMORY ponteiro 4 FALSE var2
IF var2 = este_app
    RETURN_FALSE
    RETURN
ELSE
    RETURN_TRUE
    RETURN
ENDIF

searchlight:
    READ_MEMORY ponteiro_label 2 FALSE obj
    IF DOES_OBJECT_EXIST obj 
        GET_OFFSET_FROM_OBJECT_IN_WORLD_COORDS obj 0.12 0.03 0.1 x[0] y[0] z[0]
        GET_OFFSET_FROM_CHAR_IN_WORLD_COORDS scplayer 0.0 0.0 -0.8 x[1] y[1] z[1]
        GET_OFFSET_FROM_CHAR_IN_WORLD_COORDS scplayer 0.2 5.0 0.0 x[1] y[1] z[2]
        GET_OFFSET_FROM_OBJECT_IN_WORLD_COORDS obj 5.0 -1.0 0.0 x[2] y[2] z[3]
        x[1] *= 6.0
        y[1] *= 6.0
        x[1] += x[2]
        y[1] += y[2]
        x[1] /= 7.0
        y[1] /= 7.0
        CREATE_SEARCHLIGHT x[0] y[0] z[0] x[1] y[1] z[1] 17.5 0.0 luz
    ENDIF
RETURN
}
{
    LVAR_INT ponteiro_label comando
    LVAR_INT botão[2]
    comandos:
    comando *= 2
    ponteiro_label += comando
    READ_MEMORY ponteiro_label 2 FALSE botão[0]
    ponteiro_label += 14
    READ_MEMORY ponteiro_label 2 FALSE botão[1]
    IF IS_PC_USING_JOYPAD
        IF IS_BUTTON_PRESSED 0 botão[1]//LB
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
{
getIntPoint:
    LVAR_FLOAT fromX fromY fromZ toX toY toZ
    LVAR_INT bBuilding bCar bActor bObject bParticle
    LVAR_INT ignoreEntity 
    LVAR_FLOAT intPointX intPointY intPointZ fDistance
    LVAR_INT var1 var2 var3 var4
    LVAR_INT bReturn
    LVAR_INT var5 var6 var7
    LVAR_INT var8
    IF NOT ignoreEntity = 0x0
        WRITE_MEMORY 0xB7CD68 4 ignoreEntity FALSE
    ENDIF
    GET_VAR_POINTER (fromX) (var1)  // from 0-2
    GET_VAR_POINTER (toX) (var2)    // to 3-5
    GET_VAR_POINTER (intPointX) (var3)  // out 12-14
    GET_VAR_POINTER (fDistance) (var4)  // out 15
    CALL_FUNCTION 0x56BA00 12 12 (0 0 0) (bParticle bObject bActor bCar bBuilding) (var4 var3 var2 var1) bReturn
    IF NOT ignoreEntity = 0x0
        WRITE_MEMORY 0xB7CD68 4 0 FALSE
    ENDIF
    GET_VAR_POINTER (var5) (var8)
    IF NOT bReturn = 0
        IF NOT intPointX = 0.0
        AND NOT intPointY = 0.0
        AND NOT intPointZ = 0.0
            GET_DISTANCE_BETWEEN_COORDS_3D (fromX fromY fromZ) (intPointX intPointY intPointZ) (fDistance)
        ENDIF
    ELSE
        fDistance = 0.0
    ENDIF
CLEO_RETURN 0 intPointX intPointY intPointZ fDistance
}
{
SimulateKey:
    LVAR_INT adress
    adress *= 2
    adress += 0xB73458 
    WRITE_MEMORY adress 2 0 0
CLEO_RETURN 0
}
SCRIPT_END