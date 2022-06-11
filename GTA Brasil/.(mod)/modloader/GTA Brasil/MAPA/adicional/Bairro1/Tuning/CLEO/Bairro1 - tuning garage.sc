SCRIPT_START
{
    LVAR_INT scplayer i hVeh hDoor bUsing pExternalActions bExiting
    LVAR_FLOAT f fDoorProgress fDoorZ x y z

    CONST_FLOAT DOOR_SPEED 0.015
    CONST_FLOAT DOOR_X 2550.55
    CONST_FLOAT DOOR_Y -1970.15
    CONST_FLOAT DOOR_Z 13.8
    CONST_FLOAT DOOR_OPEN_DISTANCE 2.4

    GET_PLAYER_CHAR 0 scplayer

    GET_LABEL_POINTER ExternalActions (pExternalActions)

    WHILE TRUE
        WAIT 0
        IF LOCATE_CHAR_DISTANCE_TO_COORDINATES scplayer DOOR_X DOOR_Y DOOR_Z 200.0

            CREATE_OBJECT_NO_SAVE 12382 DOOR_X DOOR_Y DOOR_Z FALSE FALSE (hDoor)
            SET_OBJECT_HEADING hDoor 0.0

            WHILE LOCATE_CHAR_DISTANCE_TO_COORDINATES scplayer DOOR_X DOOR_Y DOOR_Z 210.0
                WAIT 0

                STORE_CAR_CHAR_IS_IN_NO_SAVE scplayer (hVeh)

                IF bUsing = TRUE
                    READ_MEMORY pExternalActions 4 FALSE (i)
                    IF i = 1
                        bUsing = FALSE
                        bExiting = TRUE
                        GOSUB PlayerUnlock
                    ENDIF
                ENDIF

                IF bExiting = TRUE
                    IF NOT LOCATE_CHAR_IN_CAR_3D scplayer 2546.5491 -1970.0604 13.4 4.0 4.0 4.0 FALSE
                        bExiting = FALSE
                    ENDIF
                ENDIF

                IF LOCATE_CHAR_DISTANCE_TO_COORDINATES scplayer DOOR_X DOOR_Y DOOR_Z 7.0
                OR LOCATE_CHAR_DISTANCE_TO_COORDINATES scplayer 2545.2 -1968.4 13.5 7.0
                    GOSUB ProcessOpenDoor
                    IF bUsing = FALSE
                    AND bExiting = FALSE
                        IF IS_CHAR_SITTING_IN_CAR scplayer hVeh
                            IF LOCATE_STOPPED_CHAR_IN_CAR_3D scplayer 2546.5491 -1970.0604 13.4 3.0 3.0 3.0 TRUE
                                IF STREAM_CUSTOM_SCRIPT "Native Tuning (Junior_Djjr).cs" hVeh pExternalActions
                                    bUsing = TRUE
                                    WRITE_MEMORY pExternalActions 4 0 FALSE
                                    GOSUB PlayerLock
                                ELSE
                                    PRINT_STRING_NOW "~r~Error: Not found - Nao encontrado 'Native Tuning (Junior_Djjr).cs'" 5000
                                ENDIF
                            ENDIF
                        ENDIF
                    ENDIF
                ELSE
                    GOSUB ProcessCloseDoor
                ENDIF

            ENDWHILE

            DELETE_OBJECT hDoor
        ENDIF
    ENDWHILE

    PlayerLock:
    SET_PLAYER_CONTROL 0 OFF
    IF DOES_VEHICLE_EXIST hVeh
        FREEZE_CAR_POSITION hVeh ON
        SET_CAR_HEADING hVeh 90.0
        GET_GROUND_Z_FOR_3D_COORD 2546.5491 -1970.0604 13.4 (z)
        SET_CAR_COORDINATES hVeh 2546.5491 -1970.0604 z
        // validate and fix
        GET_CAR_COORDINATES hVeh x y z
        IF z > 15.0
            SET_CAR_COORDINATES_SIMPLE hVeh 2546.5491 -1970.0604 13.4
        ENDIF
    ELSE
        FREEZE_CHAR_POSITION scplayer ON
    ENDIF
    RETURN

    PlayerUnlock:
    SET_PLAYER_CONTROL 0 ON
    IF DOES_VEHICLE_EXIST hVeh
        FREEZE_CAR_POSITION hVeh OFF
    ELSE
        FREEZE_CHAR_POSITION scplayer OFF
    ENDIF
    RETURN

    ProcessOpenDoor:
    IF fDoorProgress < 1.0
        fDoorProgress +=@ DOOR_SPEED
        IF fDoorProgress > 1.0
            fDoorProgress = 1.0
        ENDIF
        GOSUB UpdateDoorCoord
    ENDIF
    RETURN

    ProcessCloseDoor:
    IF CLEO_CALL IsOkToMoveDoor 0 ()
        IF fDoorProgress > 0.0
            fDoorProgress -=@ DOOR_SPEED
            IF fDoorProgress < 0.0
                fDoorProgress = 0.0
            ENDIF
            GOSUB UpdateDoorCoord
        ENDIF
    ENDIF
    RETURN

    UpdateDoorCoord:
    EASE fDoorProgress EASE_MODE_CUBIC EASE_WAY_INOUT f
    f *= DOOR_OPEN_DISTANCE
    fDoorZ = DOOR_Z + f
    SET_OBJECT_COORDINATES hDoor DOOR_X DOOR_Y fDoorZ
    RETURN

}
SCRIPT_END

{
    LVAR_FLOAT f x1 y1 z1 x2 y2 z2

    IsOkToMoveDoor:
    x1 = DOOR_X - 0.5
    x2 = DOOR_X + 0.5
    y1 = DOOR_Y - 2.0
    y2 = DOOR_Y + 2.0
    z1 = DOOR_Z - DOOR_OPEN_DISTANCE
    z2 = DOOR_Z + DOOR_OPEN_DISTANCE
    IF IS_AREA_OCCUPIED (x1 y1 z1) (x2 y2 z2) FALSE TRUE TRUE FALSE FALSE
        RETURN_FALSE
    ELSE
        RETURN_TRUE
    ENDIF
    CLEO_RETURN 0 ()
}

ExternalActions:
DUMP
00 00 00 00
ENDDUMP
