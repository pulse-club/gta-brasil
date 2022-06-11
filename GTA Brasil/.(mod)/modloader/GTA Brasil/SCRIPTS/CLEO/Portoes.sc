// This script is meant to be modular, but it's not, yet...
SCRIPT_START
{
    LVAR_INT scplayer i hVeh hDoor bUsing pExternalActions bExiting
    LVAR_FLOAT f fDoorProgress fDoorX x y z

    CONST_INT MODEL_PORTAO_1_GARAGEM 12384

    CONST_FLOAT DOOR_1_X 2582.792725
    CONST_FLOAT DOOR_1_Y -1868.728882
    CONST_FLOAT DOOR_1_Z 13.83
    CONST_FLOAT DOOR_1_RADIUS 200.0

    CONST_FLOAT DOOR_SPEED 0.005
    CONST_FLOAT DOOR_OPEN_DISTANCE 4.2

    GET_PLAYER_CHAR 0 (scplayer)

    WHILE TRUE
        WAIT 0

        IF LOCATE_CAMERA_DISTANCE_TO_COORDINATES DOOR_1_X DOOR_1_Y DOOR_1_Z DOOR_1_RADIUS
            
            CREATE_OBJECT_NO_SAVE MODEL_PORTAO_1_GARAGEM DOOR_1_X DOOR_1_Y DOOR_1_Z FALSE FALSE (hDoor)
            SET_OBJECT_HEADING hDoor 0.0

            WHILE LOCATE_CAMERA_DISTANCE_TO_COORDINATES DOOR_1_X DOOR_1_Y DOOR_1_Z DOOR_1_RADIUS
                WAIT 0

                IF IS_CHAR_SITTING_IN_ANY_CAR scplayer
                    STORE_CAR_CHAR_IS_IN_NO_SAVE scplayer (hVeh)

                    IF LOCATE_CHAR_DISTANCE_TO_COORDINATES scplayer DOOR_1_X DOOR_1_Y DOOR_1_Z 7.0
                        GOSUB ProcessOpenDoor
                    ELSE
                        GOSUB ProcessCloseDoor
                    ENDIF
                ELSE
                    GOSUB ProcessCloseDoor
                ENDIF

            ENDWHILE

            DELETE_OBJECT hDoor

        ENDIF

    ENDWHILE

    
    ProcessOpenDoor:
    ///IF CLEO_CALL IsOkToMoveDoor 0 ()
    IF fDoorProgress < 1.0
        fDoorProgress +=@ DOOR_SPEED
        IF fDoorProgress > 1.0
            fDoorProgress = 1.0
        ENDIF
        GOSUB UpdateDoorCoord
    ENDIF
    //ENDIF
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
    fDoorX = DOOR_1_X + f
    SET_OBJECT_COORDINATES hDoor fDoorX DOOR_1_Y DOOR_1_Z
    RETURN

}
SCRIPT_END

{
    LVAR_FLOAT f x1 y1 z1 x2 y2 z2

    IsOkToMoveDoor:
    x1 = DOOR_1_X - DOOR_OPEN_DISTANCE
    x2 = DOOR_1_X + DOOR_OPEN_DISTANCE
    y1 = DOOR_1_Y - 0.5
    y2 = DOOR_1_Y + 0.5
    z1 = DOOR_1_Z - 1.5
    z2 = DOOR_1_Z + 1.5
    IF IS_AREA_OCCUPIED (x1 y1 z1) (x2 y2 z2) FALSE TRUE TRUE FALSE FALSE
        RETURN_FALSE
    ELSE
        RETURN_TRUE
    ENDIF
    CLEO_RETURN 0 ()
}
