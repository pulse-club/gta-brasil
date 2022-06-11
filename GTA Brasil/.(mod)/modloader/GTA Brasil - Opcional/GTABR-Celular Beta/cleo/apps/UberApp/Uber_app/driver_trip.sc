{//CLEO_CALL SendDriver 0 player pointer
SendDriver:



    LVAR_INT player pointer
    LVAR_FLOAT x y z x_node y_node z_node x_car y_car z_car heading 

    LVAR_INT p i n car driver blip b service

    CONST_FLOAT DISTANCIA_MAX_PARA_PULAR_VIAGEM 1000.0

    IF DOES_CHAR_EXIST player
    AND IS_CHAR_HEALTH_GREATER player 0
        i = pointer + UBERAPP_VIAGEM_SOLICITADA
        READ_MEMORY i 1 FALSE n
        IF n = 1

            CLEO_CALL GetCoordinatesToUberCar 0 player 100.0 x y z heading
            GOSUB SelecionarCarrosDeUber
            REQUEST_MODEL n 
            WHILE NOT HAS_MODEL_LOADED n 
                WAIT 0
            ENDWHILE
            CREATE_CAR n x y z car 
            SET_VEHICLE_DIRT_LEVEL car 0.0
            CREATE_CHAR_INSIDE_CAR car PEDTYPE_CIVMALE MALE01 driver
            SET_CHAR_CANT_BE_DRAGGED_OUT driver TRUE
            SET_CAR_HEADING car heading
            SET_CAR_CAN_BE_VISIBLY_DAMAGED car FALSE 

            ADD_BLIP_FOR_CAR car blip 
            CHANGE_BLIP_COLOUR blip 0x66b3ff //blue
            CHANGE_BLIP_DISPLAY blip BLIP_ONLY
            CHANGE_BLIP_SCALE blip 3 

            SET_CAR_CAN_GO_AGAINST_TRAFFIC car FALSE 
            SET_CAR_STAY_IN_SLOW_LANE car FALSE 
            SET_CAR_DRIVING_STYLE car DRIVINGMODE_SLOWDOWNFORCARS

            GET_CHAR_COORDINATES player x y z
            GET_CLOSEST_CAR_NODE x y z x y z  
            CAR_GOTO_COORDINATES car x y z

            ADD_BLIP_FOR_COORD x y z b 
            CHANGE_BLIP_COLOUR b GREEN
            CHANGE_BLIP_DISPLAY b BLIP_ONLY
            CHANGE_BLIP_SCALE b 2
            
            PRINT_HELP UBE16 //Espere o motorista no ponto de encontro

            WHILE GOSUB ViagemNaoForCancelada
                WAIT 0

                IF NOT IS_CAR_DEAD car 
                AND NOT IS_CHAR_DEAD driver 
                    IF LOCATE_CHAR_ANY_MEANS_CAR_2D player car 10.0 10.0 FALSE
                        CAR_SET_IDLE car 
                        IF NOT IS_HELP_MESSAGE_BEING_DISPLAYED 
                            PRINT_HELP_FOREVER UBE12 //comandos para entrar/roubar.
                        ENDIF
                    ENDIF

                    IF IS_BUTTON_PRESSED PAD1 TRIANGLE
                        x = 0.0 
                        WHILE IS_BUTTON_PRESSED PAD1 TRIANGLE
                            WAIT 0

                            x +=@ 1.0
                        ENDWHILE
                        IF x > 20.0 
                            SET_CHAR_CANT_BE_DRAGGED_OUT driver FALSE 
                            TASK_ENTER_CAR_AS_DRIVER player car -1 
                        ELSE
                            TASK_ENTER_CAR_AS_PASSENGER player car -1 -1 
                        ENDIF
                    ENDIF

                    IF IS_CHAR_IN_CAR player car
                        BREAK
                    ENDIF
                ELSE
                    BREAK
                ENDIF

            ENDWHILE

            CLEAR_HELP

            REMOVE_BLIP b
            REMOVE_BLIP blip 
            
            IF GOSUB ViagemNaoForCancelada
                IF NOT IS_CAR_DEAD car 
                AND NOT IS_CHAR_DEAD driver
                    REMOVE_BLIP b
                    REMOVE_BLIP blip 

                    GET_DRIVER_OF_CAR car n 
                    IF n = player
                        //Roubo
                        GOSUB PararViagem
                        MARK_CAR_AS_NO_LONGER_NEEDED car
                        MARK_CHAR_AS_NO_LONGER_NEEDED driver
                    ELSE
                        i = pointer + UBERAPP_X 
                        READ_MEMORY i 4 FALSE x
                        i = pointer + UBERAPP_y 
                        READ_MEMORY i 4 FALSE y
                        i = pointer + UBERAPP_z 
                        READ_MEMORY i 4 FALSE z
                        
                        GET_CLOSEST_CAR_NODE x y z x_node y_node z_node 

                        ADD_BLIP_FOR_COORD x_node y_node z_node b
                        CHANGE_BLIP_COLOUR b RED

                        i = pointer + UBERAPP_VIAGEM_EM_ANDAMENTO 
                        WRITE_MEMORY i 1 1 FALSE

                        GET_CAR_COORDINATES car x_car y_car z_car
                        GET_DISTANCE_BETWEEN_COORDS_3D x_car y_car z_car x_node y_node z_node heading

                        IF NOT heading > DISTANCIA_MAX_PARA_PULAR_VIAGEM
                            PRINT_HELP UBE14 //comandos dentro do veículo

                            SET_CAR_CRUISE_SPEED car 24.0 
                            
                            SET_CAR_TRACTION car 1.2
                            SET_CAR_DRIVING_STYLE car DRIVINGMODE_STOPFORCARS
                            CAR_GOTO_COORDINATES car x_node y_node z_node

                            GENERATE_RANDOM_INT_IN_RANGE 0 35000 timera 
                            WHILE GOSUB ViagemNaoForCancelada 
                                WAIT 0

                                IF NOT IS_CAR_DEAD car 
                                AND NOT IS_CHAR_DEAD driver
                                AND IS_CHAR_IN_CAR player car

                                    IF NOT LOCATE_CHAR_ANY_MEANS_2D player x_node y_node 12.0 12.0 FALSE

                                        IF timera > 35000
                                        AND service = 0
                                            PRINT_NOW UBE19 8000 1 
                                            PRINT_HELP UBE20
                                            service = 1
                                        ENDIF 

                                        IF service = 1
                                            IF IS_BUTTON_PRESSED PAD1 DPADRIGHT
                                                GET_CHAR_HEALTH player n 
                                                n += 3
                                                SET_CHAR_HEALTH player n 
                                            ENDIF
                                        ENDIF


                                        IF IS_BUTTON_PRESSED PAD1 RIGHTSHOULDER1 
                                            GOSUB PularViagem
                                            BREAK
                                        ENDIF

                                        IF IS_BUTTON_PRESSED PAD1 TRIANGLE
                                            CAR_SET_IDLE car 
                                            WHILE IS_CHAR_IN_CAR player car
                                                WAIT 0

                                                GET_SCRIPT_TASK_STATUS player 0x5CD n 
                                                IF n = 7 
                                                    TASK_LEAVE_CAR player car 
                                                ENDIF
                                            ENDWHILE
                                            SET_CAR_CRUISE_SPEED car 15.0
                                            CAR_WANDER_RANDOMLY car 
                                            BREAK
                                        ENDIF


                                    ELSE
                                        IF IS_THIS_HELP_MESSAGE_BEING_DISPLAYED UBE14 
                                        OR IS_THIS_HELP_MESSAGE_BEING_DISPLAYED UBE19
                                            CLEAR_HELP
                                        ENDIF

                                        IF NOT IS_HELP_MESSAGE_BEING_DISPLAYED
                                            PRINT_HELP UBE18 //Chegou ao destino
                                        ENDIF

                                    ENDIF
                                        
                                ELSE
                                    BREAK
                                ENDIF

                            ENDWHILE
                        ELSE
                            GOSUB PularViagem
                        ENDIF

                        REMOVE_BLIP b 

                        PRINT_HELP UBE17 //Viagem finalizada

                        GOSUB DescontarValorDaCorrida

                    ENDIF

                ELSE
                    PRINT_HELP UBE10 //Problema com motorista
                ENDIF
            ELSE
                PRINT_HELP UBE11 //viagem cancelada
            ENDIF

            MARK_CAR_AS_NO_LONGER_NEEDED car 
            MARK_CHAR_AS_NO_LONGER_NEEDED driver

            GOSUB PararViagem            

        ENDIF 
    ENDIF


CLEO_RETURN 0 

TERMINATE_THIS_CUSTOM_SCRIPT


SelecionarCarrosDeUber:
    GENERATE_RANDOM_INT_IN_RANGE 0 17 n
    SWITCH n 
        CASE 0 
            n = BRAVURA
            BREAK
        CASE 1 
            n = PREMIER
            BREAK
        CASE 2 
            n = HOTRINB
            BREAK
        CASE 3 
            n = BLOODRA
            BREAK
        CASE 4 
            n = NEBULA 
            BREAK
        CASE 5 
            n = WILLARD
            BREAK
        CASE 6 
            n = PRIMO 
            BREAK
        CASE 7 
            n = SUNRISE
            BREAK
        CASE 8 
            n = MERIT
            BREAK
        CASE 9 
            n = WINDSOR
            BREAK
        CASE 10 
            n = URANUS 
            BREAK
        CASE 11
            n = STAFFORD
            BREAK
        CASE 12 
            n = EMPEROR
            BREAK
        CASE 13 
            n = CLUB 
            BREAK
        CASE 14 
            n = EUROS
            BREAK
        CASE 15 
            n = PHOENIX 
            BREAK
        CASE 16 
            n = ZR350
            BREAK
    ENDSWITCH
RETURN

PularViagem:
    CAR_WANDER_RANDOMLY car
    SET_CAR_PROOFS car 1 1 1 1 1
    DO_FADE 2000 FADE_OUT
    WHILE GET_FADING_STATUS 
        WAIT 0
    ENDWHILE
    
    LOAD_SCENE x y z
    REQUEST_COLLISION x y 
    LOAD_ALL_MODELS_NOW

    SET_CAR_COORDINATES car x y z
    GET_CLOSEST_CAR_NODE_WITH_HEADING x y z x_node y_node z_node heading
    SET_CAR_COORDINATES car x_node y_node -100.0
    SET_CAR_HEADING car heading

    GET_OFFSET_FROM_CAR_IN_WORLD_COORDS car 2.0 0.0 0.0 x_node y_node z_node  
    SET_CAR_COORDINATES car x_node y_node z_node

    GET_TIME_OF_DAY n p 
    GENERATE_RANDOM_INT_IN_RANGE 2 5 i 
    n += i 
    SET_TIME_OF_DAY n p 

    WAIT 2000

    DO_FADE 2000 FADE_IN
    CAR_SET_IDLE car
    SET_CAR_TEMP_ACTION car 2 2000
    TASK_LEAVE_CAR player car
    WHILE IS_CHAR_IN_CAR player car
        WAIT 0 
    ENDWHILE
    SET_CAR_CRUISE_SPEED car 15.0
    CAR_WANDER_RANDOMLY car
    SET_CAR_PROOFS car 0 0 0 0 0
RETURN

PararViagem: 

    i = pointer + UBERAPP_VIAGEM_SOLICITADA
    WRITE_MEMORY i 1 0 FALSE
    i = pointer + UBERAPP_VIAGEM_EM_ANDAMENTO
    WRITE_MEMORY i 1 0 FALSE 
    i = pointer + UBERAPP_VIAGEM_TERMINADA
    WRITE_MEMORY i 1 0 FALSE 

RETURN

DescontarValorDaCorrida:
    i = pointer + UBERAPP_VALOR_DA_VIAGEM_SOLICITADA
    READ_MEMORY i 2 FALSE n 
    n *= -1 
    ADD_SCORE 0 n 
RETURN



ViagemNaoForCancelada:
    i = pointer + UBERAPP_VIAGEM_SOLICITADA
    READ_MEMORY i 1 FALSE n
    IF n = 1
        RETURN_TRUE
    ELSE
        RETURN_FALSE
    ENDIF
RETURN

DriverTarget:
DUMP 
00 00 00 00 
ENDDUMP 
}

{
GetCoordinatesToUberCar: 
    LVAR_INT player //in 
    LVAR_FLOAT dist

    LVAR_INT p i n s
    LVAR_FLOAT x y z x_player y_player z_player
    LVAR_FLOAT x_node y_node z_node 
    LVAR_FLOAT node_heading dist_node
    LVAR_FLOAT x_cam y_cam z_cam

    GENERATE_RANDOM_INT_IN_RANGE 0 2 p  
    IF p = 0 
        dist *= -1.0 
    ENDIF

    GET_CHAR_COORDINATES player x y z 
    GENERATE_RANDOM_INT_IN_RANGE 0 2 p 
    IF p = 0 
        x += dist 
    ELSE
        y += dist
    ENDIF 
    
    IF dist <= 0.0 
        dist *= -1.0 
    ENDIF

    n = 0
    WHILE dist_node < dist
    OR IS_POINT_ON_SCREEN x_node y_node z_node 0.0
        WAIT 0

        GET_CLOSEST_CAR_NODE_WITH_HEADING x y z x_node y_node z_node node_heading 
        IF IS_POINT_ON_SCREEN x_node y_node z_node 0.0 
            GET_ACTIVE_CAMERA_COORDINATES x_cam y_cam z_cam
            x = x_cam - dist 
            y = y_cam - dist
        ENDIF

        GET_CHAR_COORDINATES player x_player y_player z_player
        GET_DISTANCE_BETWEEN_COORDS_3D x_player y_player z_player x_node y_node z_node dist_node 
        IF n > 0 
            GENERATE_RANDOM_INT_IN_RANGE 0 2 p 
            IF p = 0 
                x -= dist 
            ELSE
                y -= dist
            ENDIF 
        ENDIF
        n += 1 
    ENDWHILE

    //PRINT_FORMATTED_NOW "Frames: %i, distreq: %0.3f, dist: %0.3f" 5000 n dist dist_node 

CLEO_RETURN 0 x_node y_node z_node node_heading
}
