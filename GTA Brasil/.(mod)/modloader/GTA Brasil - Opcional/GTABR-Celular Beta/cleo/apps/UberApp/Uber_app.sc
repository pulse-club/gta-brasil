SCRIPT_START
REQUIRE driver_trip.sc
{
//param
LVAR_INT ponteiro ponteiro_label este_app
LVAR_INT script player return_pointer var
LVAR_FLOAT float_flag x y z tam_x tam_y
LVAR_FLOAT pos_x pos_y


CLEO_CALL retornar_floats 0 ponteiro_label (pos_x pos_y tam_x tam_y)
GET_FIXED_XY_ASPECT_RATIO tam_x tam_y tam_x tam_y

CONST_INT INICIAR_TESTE 0
CONST_INT CONSULTAR_PRECO 1
CONST_INT FAZER_VIAGEM 2

CONST_INT CONSULTAR_BUTTOM 0 
CONST_INT VIAGEM_BUTTOM 1

LVAR_INT p i n item a b c d active_item
LVAR_FLOAT x_1 x_2 y_1 y_2 
LVAR_TEXT_LABEL s

LOAD_TEXTURE_DICTIONARY UberApp
LOAD_SPRITE 1 "background"
LOAD_SPRITE 2 "cell"
LOAD_SPRITE 3 "tray"
LOAD_SPRITE 4 "start_tray"
LOAD_SPRITE 5 "target_tray"
LOAD_SPRITE 6 "background_2"
LOAD_SPRITE 7 "dist_tray"
LOAD_SPRITE 8 "money_tray"

CONST_INT UBERAPP_INTERFACE 0 
CONST_INT UBERAPP_TRIP 1

SWITCH script
    CASE UBERAPP_INTERFACE
        GOTO UberAppInterface
        BREAK
    CASE UBERAPP_TRIP

        CLEO_CALL SendDriver 0 player return_pointer
        TERMINATE_THIS_CUSTOM_SCRIPT
        BREAK
ENDSWITCH

UberAppInterface:
    WHILE NOT var = este_app
        READ_MEMORY ponteiro 4 FALSE var
        WAIT 0
    ENDWHILE
    GET_PLAYER_CHAR 0 player

    GET_LABEL_POINTER UberApp_Interface p 
    i = p + UBERAPP_GUI_BACKGROUND_X_COORD 
    WRITE_MEMORY i 4 555.5 FALSE
    i = p + UBERAPP_GUI_BACKGROUND_Y_COORD
    WRITE_MEMORY i 4 335.5 FALSE
    i = p + UBERAPP_GUI_BACKGROUND_X_SIZE
    WRITE_MEMORY i 4 97.0 FALSE
    i = p + UBERAPP_GUI_BACKGROUND_Y_SIZE
    WRITE_MEMORY i 4 186.1 FALSE

    WHILE TRUE
        WAIT 0
        READ_MEMORY ponteiro 4 FALSE var
        IF GOSUB check
            GOTO UberAppInterface
        ENDIF
        IF IS_CHAR_ON_FOOT player
            WHILE IS_CHAR_HEALTH_GREATER player 0
                WAIT 0
                IF GOSUB check
                    GOTO UberAppInterface
                ENDIF

                IF CLEO_CALL comandos 0 ponteiro_label (2)//VOLTAR
                    WHILE CLEO_CALL comandos 0 ponteiro_label (2)//VOLTAR
                        WAIT 0
                    ENDWHILE
                    WRITE_MEMORY ponteiro 4 -1 FALSE
                    GOTO UberAppInterface
                ENDIF

                GET_LABEL_POINTER UberApp_Class p 
                i = p + UBERAPP_VIAGEM_SOLICITADA
                READ_MEMORY i 1 FALSE n
                IF n = 1
                    REPEAT 2 item 

                        GET_LABEL_POINTER UberApp_Interface p

                        //pagina 3 cancelar viagem
                        SWITCH item
                            CASE 0 
                                GOSUB UberAppGUI_GetBackground    
                                GET_FIXED_XY_ASPECT_RATIO x_2 y_2 x_2 y_2          
                                DRAW_SPRITE 1 x_1 y_1 x_2 y_2 255 255 225 255

                                //tray
                                i = p + UBERAPP_GUI_BACKGROUND_X_COORD
                                READ_MEMORY i 4 FALSE x_1

                                i = p + UBERAPP_GUI_BACKGROUND_Y_COORD
                                READ_MEMORY i 4 FALSE y_2
                                CLEO_CALL PorcentagemDe 0 y_2 12.0 y
                                y_1 = y_2 + y 

                                i = p + UBERAPP_GUI_BACKGROUND_X_SIZE
                                READ_MEMORY i 4 FALSE x_2
                                CLEO_CALL PorcentagemDe 0 x_2 95.0 x_2

                                i = p + UBERAPP_GUI_BACKGROUND_Y_SIZE
                                READ_MEMORY i 4 FALSE y_2
                                CLEO_CALL PorcentagemDe 0 y_2 40.0 y_2
                                
                                i = p + UBERAPP_GUI_TRAY_0_X_COORD 
                                WRITE_MEMORY i 4 x_1 FALSE
                                i = p + UBERAPP_GUI_TRAY_0_Y_COORD
                                WRITE_MEMORY i 4 y_1 FALSE

                                i = p + UBERAPP_GUI_TRAY_0_X_SIZE
                                WRITE_MEMORY i 4 x_2 FALSE
                                i = p + UBERAPP_GUI_TRAY_0_Y_SIZE
                                WRITE_MEMORY i 4 y_2 FALSE

                                GET_FIXED_XY_ASPECT_RATIO x_2 y_2 x_2 y_2
                                DRAW_SPRITE 3 x_1 y_1 x_2 y_2 255 255 255 190        
                                BREAK    
                            CASE 1                           
                                CLEO_CALL CriarBotaoEmBandeja 0 (0.0, -2.0) (90.0, 20.0) (0) (0 0 0 255)
                                //text 

                                i = p + UBERAPP_GUI_BUTTOM_0_X_SIZE
                                READ_MEMORY i 4 FALSE x_2 
                                CLEO_CALL PorcentagemDe 0 x_2 0.26 x
                                i = p + UBERAPP_GUI_BUTTOM_0_Y_SIZE
                                READ_MEMORY i 4 FALSE y_2
                                CLEO_CALL PorcentagemDe 0 y_2 4.8 y

                                GET_FIXED_XY_ASPECT_RATIO x y x y
                                SET_TEXT_SCALE x y
                                SET_TEXT_CENTRE_SIZE 640.0
                                SET_TEXT_CENTRE 1
                                SET_TEXT_DROPSHADOW 0 0 0 0 0   

                                i = p + UBERAPP_GUI_BUTTOM_0_X_COORD
                                READ_MEMORY i 4 FALSE x_1
                                i = p + UBERAPP_GUI_BUTTOM_0_Y_COORD
                                READ_MEMORY i 4 FALSE y_1
                                CLEO_CALL PorcentagemDe 0 y_1 1.0 y_2
                                y_1 = y_1 - y_2

                                DISPLAY_TEXT x_1 y_1 UBE13 //Cancelar viagem

                                n = 0
                                CLEO_CALL Active_Item_Cursor 0 active_item n
                                IF active_item = n 
                                    IF CLEO_CALL comandos 0 ponteiro_label (1)//CONFIRMAR
                                        GET_LABEL_POINTER UberApp_Class p
                                        i = p + UBERAPP_VIAGEM_SOLICITADA
                                        WRITE_MEMORY i 1 0 FALSE
                                        x = 0.0
                                        WHILE CLEO_CALL comandos 0 ponteiro_label (1)//CONFIRMAR
                                        AND x < 40.0 //transição
                                            WAIT 0
                                            x +=@ 1.0
                                            GOSUB UberAppGUI_GetBackground     
                                            GET_FIXED_XY_ASPECT_RATIO x_2 y_2 x_2 y_2         
                                            DRAW_SPRITE 1 x_1 y_1 x_2 y_2 255 255 225 255
                                        ENDWHILE
                                    ENDIF
                                ENDIF
                                BREAK
                        ENDSWITCH
                    ENDREPEAT

                    USE_TEXT_COMMANDS 1     

                    active_item = 0 

                ELSE
                    REPEAT 6 item

                        GET_LABEL_POINTER UberApp_Interface p

                        //pagina 1
                        SWITCH item
                            CASE 0 //Template
                                GOSUB UberAppGUI_GetBackground       
                                GET_FIXED_XY_ASPECT_RATIO x_2 y_2 x_2 y_2       
                                DRAW_SPRITE 1 x_1 y_1 x_2 y_2 255 255 225 255

                                //tray
                                i = p + UBERAPP_GUI_BACKGROUND_X_COORD
                                READ_MEMORY i 4 FALSE x_1 

                                i = p + UBERAPP_GUI_BACKGROUND_Y_COORD
                                READ_MEMORY i 4 FALSE y_2
                                CLEO_CALL PorcentagemDe 0 y_2 12.0 y
                                y_1 = y_2 + y 

                                i = p + UBERAPP_GUI_BACKGROUND_X_SIZE
                                READ_MEMORY i 4 FALSE x_2
                                CLEO_CALL PorcentagemDe 0 x_2 95.0 x_2

                                i = p + UBERAPP_GUI_BACKGROUND_Y_SIZE
                                READ_MEMORY i 4 FALSE y_2
                                CLEO_CALL PorcentagemDe 0 y_2 40.0 y_2
                                
                                i = p + UBERAPP_GUI_TRAY_0_X_COORD 
                                WRITE_MEMORY i 4 x_1 FALSE
                                i = p + UBERAPP_GUI_TRAY_0_Y_COORD
                                WRITE_MEMORY i 4 y_1 FALSE

                                i = p + UBERAPP_GUI_TRAY_0_X_SIZE
                                WRITE_MEMORY i 4 x_2 FALSE
                                i = p + UBERAPP_GUI_TRAY_0_Y_SIZE
                                WRITE_MEMORY i 4 y_2 FALSE
                                GET_FIXED_XY_ASPECT_RATIO x_2 y_2 x_2 y_2
                                DRAW_SPRITE 3 x_1 y_1 x_2 y_2 255 255 255 190        
                                BREAK    
                            CASE 1 // botão 0 (CONSULTAR PREÇO                           
                                CLEO_CALL CriarBotaoEmBandeja 0 (0.0, -2.0) (80.0, 20.0) (0) (0 0 0 255)
                                //text 

                                i = p + UBERAPP_GUI_BUTTOM_0_X_SIZE
                                READ_MEMORY i 4 FALSE x_2 
                                CLEO_CALL PorcentagemDe 0 x_2 0.25 x
                                i = p + UBERAPP_GUI_BUTTOM_0_Y_SIZE
                                READ_MEMORY i 4 FALSE y_2
                                CLEO_CALL PorcentagemDe 0 y_2 4.8 y

                                GET_FIXED_XY_ASPECT_RATIO x y x y
                                SET_TEXT_SCALE x y
                                SET_TEXT_CENTRE_SIZE 640.0
                                SET_TEXT_CENTRE 1
                                SET_TEXT_DROPSHADOW 0 0 0 0 0   

                                i = p + UBERAPP_GUI_BUTTOM_0_X_COORD
                                READ_MEMORY i 4 FALSE x_1
                                i = p + UBERAPP_GUI_BUTTOM_0_Y_COORD
                                READ_MEMORY i 4 FALSE y_1
                                CLEO_CALL PorcentagemDe 0 y_1 1.0 y_2
                                y_1 = y_1 - y_2

                                DISPLAY_TEXT x_1 y_1 UBE4 //Consultar preco

                                
                                n = CONSULTAR_BUTTOM
                                CLEO_CALL Active_Item_Cursor 0 active_item n
                                IF active_item = n
                                    IF CLEO_CALL comandos 0 ponteiro_label (1)//CONFIRMAR
                                        WHILE IS_CHAR_HEALTH_GREATER player 0
                                            IF CLEO_CALL comandos 0 ponteiro_label (2)//VOLTAR
                                                BREAK
                                            ENDIF
                                            WAIT 0 
                                            IF GOSUB check
                                                GOTO UberAppInterface
                                            ENDIF
                                            //pagina 1

                                            REPEAT 6 item
                                                GET_LABEL_POINTER UberApp_Interface p 

                                                SWITCH item
                                                    CASE 0 
                                                        GOSUB UberAppGUI_GetBackground    
                                                        GET_FIXED_XY_ASPECT_RATIO x_2 y_2 x_2 y_2          
                                                        DRAW_SPRITE 6 x_1 y_1 x_2 y_2 255 255 225 255
                                                        BREAK
                                                    CASE 1
                                                        CLEO_CALL CriarBotaoEmBackground 0 (0.0 -1.5) (100.0 70.0) 3 (255 255 255 190)
                                                        BREAK
                                                    CASE 2
                                                        //tray
                                                        i = p + UBERAPP_GUI_BACKGROUND_X_COORD
                                                        READ_MEMORY i 4 FALSE x_1 

                                                        i = p + UBERAPP_GUI_BACKGROUND_Y_COORD
                                                        READ_MEMORY i 4 FALSE y_2
                                                        CLEO_CALL PorcentagemDe 0 y_2 -10.5 y
                                                        y_1 = y_2 + y 

                                                        i = p + UBERAPP_GUI_BACKGROUND_X_SIZE
                                                        READ_MEMORY i 4 FALSE x_2
                                                        CLEO_CALL PorcentagemDe 0 x_2 95.0 x_2

                                                        i = p + UBERAPP_GUI_BACKGROUND_Y_SIZE
                                                        READ_MEMORY i 4 FALSE y_2
                                                        CLEO_CALL PorcentagemDe 0 y_2 10.0 y_2
                                                        
                                                        i = p + UBERAPP_GUI_TRAY_0_X_COORD 
                                                        WRITE_MEMORY i 4 x_1 FALSE
                                                        i = p + UBERAPP_GUI_TRAY_0_Y_COORD
                                                        WRITE_MEMORY i 4 y_1 FALSE

                                                        i = p + UBERAPP_GUI_TRAY_0_X_SIZE
                                                        WRITE_MEMORY i 4 x_2 FALSE
                                                        i = p + UBERAPP_GUI_TRAY_0_Y_SIZE
                                                        WRITE_MEMORY i 4 y_2 FALSE
                                                        GET_FIXED_XY_ASPECT_RATIO x_2 y_2 x_2 y_2
                                                        DRAW_SPRITE 4 x_1 y_1 x_2 y_2 255 255 255 190 

                                                        //text
                                                        i = p + UBERAPP_GUI_TRAY_0_X_SIZE
                                                        READ_MEMORY i 4 FALSE x_2 
                                                        CLEO_CALL PorcentagemDe 0 x_2 0.2 x
                                                        i = p + UBERAPP_GUI_TRAY_0_Y_SIZE
                                                        READ_MEMORY i 4 FALSE y_2
                                                        CLEO_CALL PorcentagemDe 0 y_2 3.8 y
                                                        
                                                        GET_FIXED_XY_ASPECT_RATIO x y x y
                                                        SET_TEXT_SCALE x y
                                                        SET_TEXT_CENTRE_SIZE 640.0
                                                        SET_TEXT_CENTRE 1
                                                        SET_TEXT_DROPSHADOW 0 0 0 0 0   
                                                        SET_TEXT_COLOUR 0 0 0 255

                                                        i = p + UBERAPP_GUI_TRAY_0_X_COORD
                                                        READ_MEMORY i 4 FALSE x_1
                                                        i = p + UBERAPP_GUI_TRAY_0_Y_COORD
                                                        READ_MEMORY i 4 FALSE y_1
                                                        CLEO_CALL PorcentagemDe 0 y_1 1.0 y_2
                                                        y_1 = y_1 - y_2
                                                        

                                                        GET_CHAR_COORDINATES player x y z 
                                                        GET_NAME_OF_ZONE x y z s 
                                                        DISPLAY_TEXT x_1 y_1 $s //De: Puta que Pariu                                                                            
                                                        BREAK
                                                    CASE 3

                                                        //tray
                                                        i = p + UBERAPP_GUI_BACKGROUND_X_COORD
                                                        READ_MEMORY i 4 FALSE x_1 

                                                        i = p + UBERAPP_GUI_BACKGROUND_Y_COORD
                                                        READ_MEMORY i 4 FALSE y_2
                                                        CLEO_CALL PorcentagemDe 0 y_2 -3.5 y
                                                        y_1 = y_2 + y 

                                                        i = p + UBERAPP_GUI_BACKGROUND_X_SIZE
                                                        READ_MEMORY i 4 FALSE x_2
                                                        CLEO_CALL PorcentagemDe 0 x_2 95.0 x_2

                                                        i = p + UBERAPP_GUI_BACKGROUND_Y_SIZE
                                                        READ_MEMORY i 4 FALSE y_2
                                                        CLEO_CALL PorcentagemDe 0 y_2 10.0 y_2
                                                        
                                                        i = p + UBERAPP_GUI_TRAY_0_X_COORD 
                                                        WRITE_MEMORY i 4 x_1 FALSE
                                                        i = p + UBERAPP_GUI_TRAY_0_Y_COORD
                                                        WRITE_MEMORY i 4 y_1 FALSE

                                                        i = p + UBERAPP_GUI_TRAY_0_X_SIZE
                                                        WRITE_MEMORY i 4 x_2 FALSE
                                                        i = p + UBERAPP_GUI_TRAY_0_Y_SIZE
                                                        WRITE_MEMORY i 4 y_2 FALSE

                                                        GET_FIXED_XY_ASPECT_RATIO x_2 y_2 x_2 y_2
                                                        DRAW_SPRITE 5 x_1 y_1 x_2 y_2 255 255 255 190 

                                                        //text
                                                        i = p + UBERAPP_GUI_TRAY_0_X_SIZE
                                                        READ_MEMORY i 4 FALSE x_2 
                                                        CLEO_CALL PorcentagemDe 0 x_2 0.2 x
                                                        i = p + UBERAPP_GUI_TRAY_0_Y_SIZE
                                                        READ_MEMORY i 4 FALSE y_2
                                                        CLEO_CALL PorcentagemDe 0 y_2 3.8 y

                                                        GET_FIXED_XY_ASPECT_RATIO x y x y
                                                        SET_TEXT_SCALE x y
                                                        SET_TEXT_CENTRE_SIZE 640.0
                                                        SET_TEXT_CENTRE 1
                                                        SET_TEXT_DROPSHADOW 0 0 0 0 0   
                                                        SET_TEXT_COLOUR 0 0 0 255

                                                        i = p + UBERAPP_GUI_TRAY_0_X_COORD
                                                        READ_MEMORY i 4 FALSE x_1
                                                        i = p + UBERAPP_GUI_TRAY_0_Y_COORD
                                                        READ_MEMORY i 4 FALSE y_1
                                                        CLEO_CALL PorcentagemDe 0 y_1 1.0 y_2
                                                        y_1 = y_1 - y_2
                                                        

                                                        GET_TARGET_BLIP_COORDS x y z 
                                                        GET_NAME_OF_ZONE x y z s 
                                                        DISPLAY_TEXT x_1 y_1 $s 
                                                        BREAK
                                                    CASE 4
                                                        //tray
                                                        i = p + UBERAPP_GUI_BACKGROUND_X_COORD
                                                        READ_MEMORY i 4 FALSE x_1 

                                                        i = p + UBERAPP_GUI_BACKGROUND_Y_COORD
                                                        READ_MEMORY i 4 FALSE y_2
                                                        CLEO_CALL PorcentagemDe 0 y_2 3.5 y
                                                        y_1 = y_2 + y 

                                                        i = p + UBERAPP_GUI_BACKGROUND_X_SIZE
                                                        READ_MEMORY i 4 FALSE x_2
                                                        CLEO_CALL PorcentagemDe 0 x_2 95.0 x_2

                                                        i = p + UBERAPP_GUI_BACKGROUND_Y_SIZE
                                                        READ_MEMORY i 4 FALSE y_2
                                                        CLEO_CALL PorcentagemDe 0 y_2 10.0 y_2
                                                        
                                                        i = p + UBERAPP_GUI_TRAY_0_X_COORD 
                                                        WRITE_MEMORY i 4 x_1 FALSE
                                                        i = p + UBERAPP_GUI_TRAY_0_Y_COORD
                                                        WRITE_MEMORY i 4 y_1 FALSE

                                                        i = p + UBERAPP_GUI_TRAY_0_X_SIZE
                                                        WRITE_MEMORY i 4 x_2 FALSE
                                                        i = p + UBERAPP_GUI_TRAY_0_Y_SIZE
                                                        WRITE_MEMORY i 4 y_2 FALSE

                                                        GET_FIXED_XY_ASPECT_RATIO x_2 y_2 x_2 y_2
                                                        DRAW_SPRITE 7 x_1 y_1 x_2 y_2 255 255 255 190 

                                                        //text
                                                        i = p + UBERAPP_GUI_TRAY_0_X_SIZE
                                                        READ_MEMORY i 4 FALSE x_2 
                                                        CLEO_CALL PorcentagemDe 0 x_2 0.2 x
                                                        i = p + UBERAPP_GUI_TRAY_0_Y_SIZE
                                                        READ_MEMORY i 4 FALSE y_2
                                                        CLEO_CALL PorcentagemDe 0 y_2 3.8 y

                                                        GET_FIXED_XY_ASPECT_RATIO x y x y
                                                        SET_TEXT_SCALE x y
                                                        SET_TEXT_CENTRE_SIZE 640.0
                                                        SET_TEXT_CENTRE 1
                                                        SET_TEXT_DROPSHADOW 0 0 0 0 0   
                                                        SET_TEXT_COLOUR 0 0 0 255

                                                        i = p + UBERAPP_GUI_TRAY_0_X_COORD
                                                        READ_MEMORY i 4 FALSE x_1
                                                        i = p + UBERAPP_GUI_TRAY_0_Y_COORD
                                                        READ_MEMORY i 4 FALSE y_1
                                                        CLEO_CALL PorcentagemDe 0 y_1 1.0 y_2
                                                        y_1 = y_1 - y_2
                                                        
                                                    
                                                        CLEO_CALL UberApp_Calculator 0 player 50.0

                                                        GET_LABEL_POINTER UberApp_Class return_pointer
                                                        i = return_pointer + UBERAPP_DISTANCIA_DD
                                                        READ_MEMORY i 4 FALSE x
                                                        STRING_FORMAT s "%0.2f km" x
                                                        ADD_TEXT_LABEL UBE8 $s
                                                        DISPLAY_TEXT x_1 y_1 UBE8 
                                                        BREAK
                                                    CASE 5
                                                        //tray
                                                        i = p + UBERAPP_GUI_BACKGROUND_X_COORD
                                                        READ_MEMORY i 4 FALSE x_1 

                                                        i = p + UBERAPP_GUI_BACKGROUND_Y_COORD
                                                        READ_MEMORY i 4 FALSE y_2
                                                        CLEO_CALL PorcentagemDe 0 y_2 9.5 y
                                                        y_1 = y_2 + y 

                                                        i = p + UBERAPP_GUI_BACKGROUND_X_SIZE
                                                        READ_MEMORY i 4 FALSE x_2
                                                        CLEO_CALL PorcentagemDe 0 x_2 95.0 x_2

                                                        i = p + UBERAPP_GUI_BACKGROUND_Y_SIZE
                                                        READ_MEMORY i 4 FALSE y_2
                                                        CLEO_CALL PorcentagemDe 0 y_2 10.0 y_2
                                                                                                                
                                                        i = p + UBERAPP_GUI_TRAY_0_X_COORD 
                                                        WRITE_MEMORY i 4 x_1 FALSE
                                                        i = p + UBERAPP_GUI_TRAY_0_Y_COORD
                                                        WRITE_MEMORY i 4 y_1 FALSE

                                                        i = p + UBERAPP_GUI_TRAY_0_X_SIZE
                                                        WRITE_MEMORY i 4 x_2 FALSE
                                                        i = p + UBERAPP_GUI_TRAY_0_Y_SIZE
                                                        WRITE_MEMORY i 4 y_2 FALSE 
                                                        GET_FIXED_XY_ASPECT_RATIO x_2 y_2 x_2 y_2
                                                        DRAW_SPRITE 8 x_1 y_1 x_2 y_2 255 255 255 190 

                                                        //text
                                                        i = p + UBERAPP_GUI_TRAY_0_X_SIZE
                                                        READ_MEMORY i 4 FALSE x_2 
                                                        CLEO_CALL PorcentagemDe 0 x_2 0.2 x
                                                        i = p + UBERAPP_GUI_TRAY_0_Y_SIZE
                                                        READ_MEMORY i 4 FALSE y_2
                                                        CLEO_CALL PorcentagemDe 0 y_2 3.8 y

                                                        GET_FIXED_XY_ASPECT_RATIO x y x y
                                                        SET_TEXT_SCALE x y
                                                        SET_TEXT_CENTRE_SIZE 640.0
                                                        SET_TEXT_CENTRE 1
                                                        SET_TEXT_DROPSHADOW 0 0 0 0 0   
                                                        SET_TEXT_COLOUR 0 0 0 255

                                                        i = p + UBERAPP_GUI_TRAY_0_X_COORD
                                                        READ_MEMORY i 4 FALSE x_1
                                                        i = p + UBERAPP_GUI_TRAY_0_Y_COORD
                                                        READ_MEMORY i 4 FALSE y_1
                                                        CLEO_CALL PorcentagemDe 0 y_1 1.0 y_2
                                                        y_1 = y_1 - y_2

                                                        CLEO_CALL UberApp_Calculator 0 player 50.0 

                                                        GET_LABEL_POINTER UberApp_Class return_pointer
                                                        i = return_pointer + UBERAPP_VALOR_DA_CORRIDA_DD
                                                        READ_MEMORY i 2 FALSE n
                                                        STRING_FORMAT s "R$ %i" n
                                                        ADD_TEXT_LABEL UBE9 $s
                                                        DISPLAY_TEXT x_1 y_1 UBE9 
                                                        BREAK
                                                ENDSWITCH
                                            ENDREPEAT

                                            USE_TEXT_COMMANDS 1
                                        ENDWHILE
                                        x = 0.0
                                        IF CLEO_CALL comandos 0 ponteiro_label (2)//VOLTAR
                                            WHILE CLEO_CALL comandos 0 ponteiro_label (2)//VOLTAR
                                                WAIT 0
                                                IF x > 40.0
                                                    BREAK
                                                ENDIF
                                                x +=@ 1.0
                                                GOSUB UberAppGUI_GetBackground      
                                                GET_FIXED_XY_ASPECT_RATIO x_2 y_2 x_2 y_2        
                                                DRAW_SPRITE 1 x_1 y_1 x_2 y_2 255 255 225 255
                                            ENDWHILE
                                        ENDIF
                                    ENDIF
                                ENDIF
                                BREAK
                            CASE 2 //buttom 1 (FAZER VIAGEM
                                CLEO_CALL CriarBotaoEmBandeja 0 (0.0, 4.0) (80.0, 20.0) (0) (0 0 0 255)

                                //text

                                i = p + UBERAPP_GUI_BUTTOM_0_X_SIZE
                                READ_MEMORY i 4 FALSE x_2 
                                CLEO_CALL PorcentagemDe 0 x_2 0.26 x
                                i = p + UBERAPP_GUI_BUTTOM_0_Y_SIZE
                                READ_MEMORY i 4 FALSE y_2
                                CLEO_CALL PorcentagemDe 0 y_2 4.8 y

                                GET_FIXED_XY_ASPECT_RATIO x y x y
                                SET_TEXT_SCALE x y
                                SET_TEXT_CENTRE_SIZE 640.0
                                SET_TEXT_CENTRE 1
                                SET_TEXT_DROPSHADOW 0 0 0 0 0   

                                i = p + UBERAPP_GUI_BUTTOM_0_X_COORD
                                READ_MEMORY i 4 FALSE x_1
                                i = p + UBERAPP_GUI_BUTTOM_0_Y_COORD
                                READ_MEMORY i 4 FALSE y_1
                                CLEO_CALL PorcentagemDe 0 y_1 1.0 y_2
                                y_1 = y_1 - y_2

                                DISPLAY_TEXT x_1 y_1 UBE5 //Fazer viagem

                                n = VIAGEM_BUTTOM
                                CLEO_CALL Active_Item_Cursor 0 active_item n

                                IF active_item = n
                                    IF CLEO_CALL comandos 0 ponteiro_label (1)//CONFIRMAR
                                        GET_TARGET_BLIP_COORDS x y z 


                                        CLEO_CALL UberApp_Calculator 0 player 50.0

                                        GET_LABEL_POINTER UberApp_Class p 
                                        i = p + UBERAPP_VIAGEM_SOLICITADA
                                        WRITE_MEMORY i 1 1 FALSE
                                        i = p + UBERAPP_VALOR_DA_CORRIDA_DD
                                        READ_MEMORY i 2 FALSE a
                                        i = p + UBERAPP_VALOR_DA_VIAGEM_SOLICITADA
                                        WRITE_MEMORY i 2 a FALSE

                                        i = p + UBERAPP_X 
                                        WRITE_MEMORY i 4 x FALSE
                                        i = p + UBERAPP_Y 
                                        WRITE_MEMORY i 4 y FALSE                                            
                                        i = p + UBERAPP_Z 
                                        WRITE_MEMORY i 4 z FALSE

                                        script = UBERAPP_TRIP
                                        STREAM_CUSTOM_SCRIPT "apps\UberApp\Uber_app.cs" ponteiro ponteiro_label este_app script player p

                                        timera = 0
                                        WHILE NOT CLEO_CALL comandos 0 ponteiro_label (2)//VOLTAR
                                        AND IS_CHAR_HEALTH_GREATER player 0
                                        AND NOT timera > 2000
                                            WAIT 0 

                                            GET_LABEL_POINTER UberApp_Interface p

                                            REPEAT 3 item
                                                SWITCH item 
                                                    CASE 0 
                                                        GOSUB UberAppGUI_GetBackground      
                                                        GET_FIXED_XY_ASPECT_RATIO x_2 y_2 x_2 y_2
                                                        DRAW_SPRITE 6 x_1 y_1 x_2 y_2 255 255 225 255
                                                        BREAK
                                                    CASE 1
                                                        CLEO_CALL CriarBotaoEmBackground 0 (0.0 -1.5) (60.0 10.0) 3 (255 255 255 0)
                                                        BREAK                                         
                                                    CASE 2 
                                                        i = p + UBERAPP_GUI_BUTTOM_0_X_SIZE
                                                        READ_MEMORY i 4 FALSE x_2 
                                                        CLEO_CALL PorcentagemDe 0 x_2 0.4 x
                                                        i = p + UBERAPP_GUI_BUTTOM_0_Y_SIZE
                                                        READ_MEMORY i 4 FALSE y_2
                                                        CLEO_CALL PorcentagemDe 0 y_2 4.8 y

                                                        GET_FIXED_XY_ASPECT_RATIO x y x y
                                                        SET_TEXT_SCALE x y
                                                        SET_TEXT_CENTRE_SIZE 640.0
                                                        SET_TEXT_CENTRE 1
                                                        SET_TEXT_DROPSHADOW 0 0 0 0 0   
                                                        SET_TEXT_COLOUR 0 0 0 255

                                                        i = p + UBERAPP_GUI_BUTTOM_0_X_COORD
                                                        READ_MEMORY i 4 FALSE x_1
                                                        i = p + UBERAPP_GUI_BUTTOM_0_Y_COORD
                                                        READ_MEMORY i 4 FALSE y_1
                                                        CLEO_CALL PorcentagemDe 0 y_1 1.0 y_2
                                                        y_1 = y_1 - y_2

                                                        DISPLAY_TEXT x_1 y_1 UBE7 //Motorista á caminho

                                                        BREAK
                                                ENDSWITCH
                                            ENDREPEAT

                                            USE_TEXT_COMMANDS 1 
                                        ENDWHILE

                                        IF CLEO_CALL comandos 0 ponteiro_label (2)//VOLTAR
                                            WHILE CLEO_CALL comandos 0 ponteiro_label (2)//VOLTAR
                                                WAIT 0
                                                IF x > 40.0
                                                    BREAK
                                                ENDIF
                                                x +=@ 1.0
                                                GOSUB UberAppGUI_GetBackground      
                                                GET_FIXED_XY_ASPECT_RATIO x_2 y_2 x_2 y_2        
                                                DRAW_SPRITE 1 x_1 y_1 x_2 y_2 255 255 225 255
                                            ENDWHILE
                                        ENDIF
                                    ENDIF
                                ENDIF


                                BREAK
                            CASE 3 //Partida
                                
                                //tray
                                i = p + UBERAPP_GUI_BACKGROUND_X_COORD
                                READ_MEMORY i 4 FALSE x_1 

                                i = p + UBERAPP_GUI_BACKGROUND_Y_COORD
                                READ_MEMORY i 4 FALSE y_2
                                CLEO_CALL PorcentagemDe 0 y_2 -13.5 y
                                y_1 = y_2 + y 

                                i = p + UBERAPP_GUI_BACKGROUND_X_SIZE
                                READ_MEMORY i 4 FALSE x_2
                                CLEO_CALL PorcentagemDe 0 x_2 95.0 x_2

                                i = p + UBERAPP_GUI_BACKGROUND_Y_SIZE
                                READ_MEMORY i 4 FALSE y_2
                                CLEO_CALL PorcentagemDe 0 y_2 10.0 y_2
                                
                                i = p + UBERAPP_GUI_TRAY_0_X_COORD 
                                WRITE_MEMORY i 4 x_1 FALSE
                                i = p + UBERAPP_GUI_TRAY_0_Y_COORD
                                WRITE_MEMORY i 4 y_1 FALSE

                                i = p + UBERAPP_GUI_TRAY_0_X_SIZE
                                WRITE_MEMORY i 4 x_2 FALSE
                                i = p + UBERAPP_GUI_TRAY_0_Y_SIZE
                                WRITE_MEMORY i 4 y_2 FALSE
                                GET_FIXED_XY_ASPECT_RATIO x_2 y_2 x_2 y_2
                                DRAW_SPRITE 4 x_1 y_1 x_2 y_2 255 255 255 190 

                                //text
                                i = p + UBERAPP_GUI_TRAY_0_X_SIZE
                                READ_MEMORY i 4 FALSE x_2 
                                CLEO_CALL PorcentagemDe 0 x_2 0.2 x
                                i = p + UBERAPP_GUI_TRAY_0_Y_SIZE
                                READ_MEMORY i 4 FALSE y_2
                                CLEO_CALL PorcentagemDe 0 y_2 3.8 y

                                GET_FIXED_XY_ASPECT_RATIO x y x y
                                SET_TEXT_SCALE x y
                                SET_TEXT_CENTRE_SIZE 640.0
                                SET_TEXT_CENTRE 1
                                SET_TEXT_DROPSHADOW 0 0 0 0 0   
                                SET_TEXT_COLOUR 0 0 0 255

                                i = p + UBERAPP_GUI_TRAY_0_X_COORD
                                READ_MEMORY i 4 FALSE x_1
                                i = p + UBERAPP_GUI_TRAY_0_Y_COORD
                                READ_MEMORY i 4 FALSE y_1
                                CLEO_CALL PorcentagemDe 0 y_1 1.0 y_2
                                y_1 = y_1 - y_2
                                
                                GET_CHAR_COORDINATES player x y z 
                                GET_NAME_OF_ZONE x y z s 
                                DISPLAY_TEXT x_1 y_1 $s //De: Puta que Pariu
                                BREAK
                            
                            
                            CASE 4 //Destino

                                //tray
                                i = p + UBERAPP_GUI_BACKGROUND_X_COORD
                                READ_MEMORY i 4 FALSE x_1 

                                i = p + UBERAPP_GUI_BACKGROUND_Y_COORD
                                READ_MEMORY i 4 FALSE y_2
                                CLEO_CALL PorcentagemDe 0 y_2 -6.5 y
                                y_1 = y_2 + y 

                                i = p + UBERAPP_GUI_BACKGROUND_X_SIZE
                                READ_MEMORY i 4 FALSE x_2
                                CLEO_CALL PorcentagemDe 0 x_2 95.0 x_2

                                i = p + UBERAPP_GUI_BACKGROUND_Y_SIZE
                                READ_MEMORY i 4 FALSE y_2
                                CLEO_CALL PorcentagemDe 0 y_2 10.0 y_2
                                
                                i = p + UBERAPP_GUI_TRAY_0_X_COORD 
                                WRITE_MEMORY i 4 x_1 FALSE
                                i = p + UBERAPP_GUI_TRAY_0_Y_COORD
                                WRITE_MEMORY i 4 y_1 FALSE

                                i = p + UBERAPP_GUI_TRAY_0_X_SIZE
                                WRITE_MEMORY i 4 x_2 FALSE
                                i = p + UBERAPP_GUI_TRAY_0_Y_SIZE
                                WRITE_MEMORY i 4 y_2 FALSE
                                GET_FIXED_XY_ASPECT_RATIO x_2 y_2 x_2 y_2
                                DRAW_SPRITE 5 x_1 y_1 x_2 y_2 255 255 255 190 

                                //text
                                i = p + UBERAPP_GUI_TRAY_0_X_SIZE
                                READ_MEMORY i 4 FALSE x_2 
                                CLEO_CALL PorcentagemDe 0 x_2 0.2 x
                                i = p + UBERAPP_GUI_TRAY_0_Y_SIZE
                                READ_MEMORY i 4 FALSE y_2
                                CLEO_CALL PorcentagemDe 0 y_2 3.8 y

                                GET_FIXED_XY_ASPECT_RATIO x y x y
                                SET_TEXT_SCALE x y
                                SET_TEXT_CENTRE_SIZE 640.0
                                SET_TEXT_CENTRE 1
                                SET_TEXT_DROPSHADOW 0 0 0 0 0   
                                SET_TEXT_COLOUR 0 0 0 255

                                i = p + UBERAPP_GUI_TRAY_0_X_COORD
                                READ_MEMORY i 4 FALSE x_1
                                i = p + UBERAPP_GUI_TRAY_0_Y_COORD
                                READ_MEMORY i 4 FALSE y_1
                                CLEO_CALL PorcentagemDe 0 y_1 1.0 y_2
                                y_1 = y_1 - y_2
                                
                                GET_TARGET_BLIP_COORDS x y z 
                                GET_NAME_OF_ZONE x y z s 
                                DISPLAY_TEXT x_1 y_1 $s 
                                BREAK
                        ENDSWITCH

                    ENDREPEAT

                    IF CLEO_CALL comandos 0 ponteiro_label 4//cima
                        active_item -= 1
                    ENDIF

                    IF CLEO_CALL comandos 0 ponteiro_label 5//baixo
                        active_item += 1
                    ENDIF
                    
                    IF active_item > VIAGEM_BUTTOM
                        active_item = VIAGEM_BUTTOM
                    ENDIF

                    IF active_item < CONSULTAR_BUTTOM
                        active_item = CONSULTAR_BUTTOM 
                    ENDIF
                ENDIF

                CLEO_CALL RedimensionarApp 0

                
                USE_TEXT_COMMANDS 1

            ENDWHILE

        ENDIF

    ENDWHILE


        UberAppGUI_GetBackground:
            GET_LABEL_POINTER UberApp_Interface p 
            i = p + UBERAPP_GUI_BACKGROUND_X_COORD
            READ_MEMORY i 4 FALSE x_1
            i = p + UBERAPP_GUI_BACKGROUND_Y_COORD
            READ_MEMORY i 4 FALSE y_1
            i = p + UBERAPP_GUI_BACKGROUND_X_SIZE
            READ_MEMORY i 4 FALSE x_2 
            i = p + UBERAPP_GUI_BACKGROUND_Y_SIZE
            READ_MEMORY i 4 FALSE y_2

            /*
            i = p + UBERAPP_GUI_BACKGOUND_RGBA_START
            READ_MEMORY i 1 FALSE a 
            i += 1
            READ_MEMORY i 1 FALSE b
            i += 1
            READ_MEMORY i 1 FALSE c
            i += 1
            READ_MEMORY i 1 FALSE d
            */
        RETURN 

        UberAppGUI_GetButtom_0:
            GET_LABEL_POINTER UberApp_Interface p 
            i = p + UBERAPP_GUI_BUTTOM_0_X_COORD
            READ_MEMORY i 4 FALSE x_1
            i = p + UBERAPP_GUI_BUTTOM_0_Y_COORD
            READ_MEMORY i 4 FALSE y_1
            i = p + UBERAPP_GUI_BUTTOM_0_X_SIZE
            READ_MEMORY i 4 FALSE x_2 
            i = p + UBERAPP_GUI_BUTTOM_0_Y_SIZE
            READ_MEMORY i 4 FALSE y_2

            /*
            i = p + UBERAPP_GUI_BUTTOM_0_RGBA_START
            READ_MEMORY i 1 FALSE a 
            i += 1
            READ_MEMORY i 1 FALSE b
            i += 1
            READ_MEMORY i 1 FALSE c
            i += 1
            READ_MEMORY i 1 FALSE d
            */
        RETURN

        check:
        READ_MEMORY ponteiro 4 FALSE var
        IF var = este_app
            RETURN_FALSE
            RETURN
        ELSE
            RETURN_TRUE
            RETURN
        ENDIF

        UberApp_Interface:
            CONST_INT UBERAPP_GUI_BACKGROUND_X_COORD 0
            CONST_INT UBERAPP_GUI_BACKGROUND_Y_COORD 4
            CONST_INT UBERAPP_GUI_BACKGROUND_X_SIZE 8
            CONST_INT UBERAPP_GUI_BACKGROUND_Y_SIZE 12

            //CONST_INT UBERAPP_GUI_BACKGOUND_RGBA_START 13

            CONST_INT UBERAPP_GUI_BUTTOM_0_X_COORD 16 
            CONST_INT UBERAPP_GUI_BUTTOM_0_Y_COORD 20
            CONST_INT UBERAPP_GUI_BUTTOM_0_X_SIZE 24
            CONST_INT UBERAPP_GUI_BUTTOM_0_Y_SIZE 28 

            //CONST_INT UBERAPP_GUI_BUTTOM_0_RGBA_START 29

            CONST_INT UBERAPP_GUI_BUTTOM_0_X_TEXT_SCALE 32
            CONST_INT UBERAPP_GUI_BUTTOM_0_Y_TEXT_SCALE 36

            CONST_INT UBERAPP_GUI_TRAY_0_X_COORD 40
            CONST_INT UBERAPP_GUI_TRAY_0_Y_COORD 44
            CONST_INT UBERAPP_GUI_TRAY_0_X_SIZE 48
            CONST_INT UBERAPP_GUI_TRAY_0_Y_SIZE 52


            DUMP
            //BackGround
            00 00 00 00 //x_coord
            00 00 00 00 //y_coord

            00 00 00 00 //x_size
            00 00 00 00 //y_size

            //00 00 00 00 //RGBA

            //Buttom 0
            00 00 00 00 //x_coord
            00 00 00 00 //y_coord

            00 00 00 00 //x_size
            00 00 00 00 //y_size

            00 00 00 00 //text_scale x
            00 00 00 00 //text_scale y

            //tray - 1
            00 00 00 00 //x_coord
            00 00 00 00 //y_coord

            00 00 00 00 //x_size
            00 00 00 00 //y_size
            ENDDUMP


    PRINT_STRING_NOW "App Uber finalizado" 2000
TERMINATE_THIS_CUSTOM_SCRIPT

}

{
RedimensionarApp:
    LVAR_FLOAT x y
    LVAR_INT p i n

    GET_LABEL_POINTER UberApp_Interface p

    IF IS_KEY_PRESSED VK_NUMPAD4  
        i = p + UBERAPP_GUI_BACKGROUND_X_SIZE
        READ_MEMORY i 4 FALSE x
        x -= 1.0
        WRITE_MEMORY i 4 x FALSE
    ENDIF

    IF IS_KEY_PRESSED VK_NUMPAD6  
        i = p + UBERAPP_GUI_BACKGROUND_X_SIZE
        READ_MEMORY i 4 FALSE x
        x += 1.0
        WRITE_MEMORY i 4 x FALSE
    ENDIF

    IF IS_KEY_PRESSED VK_NUMPAD8 
        i = p + UBERAPP_GUI_BACKGROUND_Y_SIZE
        READ_MEMORY i 4 FALSE y
        y -= 1.0
        WRITE_MEMORY i 4 y FALSE
    ENDIF

    IF IS_KEY_PRESSED VK_NUMPAD2 
        i = p + UBERAPP_GUI_BACKGROUND_Y_SIZE
        READ_MEMORY i 4 FALSE y
        y += 1.0
        WRITE_MEMORY i 4 y FALSE
    ENDIF

    IF IS_KEY_PRESSED VK_NUMPAD5
        i = p + UBERAPP_GUI_BACKGROUND_X_SIZE
        WRITE_MEMORY i 4 75.0 FALSE
        i = p + UBERAPP_GUI_BACKGROUND_Y_SIZE
        WRITE_MEMORY i 4 150.0 FALSE
    ENDIF


    //PRINT_FORMATTED_NOW "Size: X = %0.3f, Y = %0.3f" 1 x y 
CLEO_RETURN 0 
}

{//CLEO_CALL CriarBotaoEmBandeja 0 (0.0, -2.0) (80.0, 20.0) (sprite, r g b a)
CriarBotaoEmBackground: 
    LVAR_FLOAT dist_coord_x dist_coord_y
    LVAR_FLOAT rel_x_size rel_y_size
    LVAR_INT sprite r g b a
    
    LVAR_INT p i n 
    LVAR_FLOAT x_1 y_1 x_2 y_2 x y

    GET_LABEL_POINTER UberApp_Interface p 

    //x_coord
    i = p + UBERAPP_GUI_BACKGROUND_X_COORD
    READ_MEMORY i 4 FALSE x_1
    CLEO_CALL PorcentagemDe 0 x_1 dist_coord_x y
    y_1 = y_1 + y

    //y_coord
    i = p + UBERAPP_GUI_BACKGROUND_Y_COORD
    READ_MEMORY i 4 FALSE y_1
    CLEO_CALL PorcentagemDe 0 y_1 dist_coord_y y
    y_1 = y_1 + y

    //x_size
    i = p + UBERAPP_GUI_BACKGROUND_X_SIZE
    READ_MEMORY i 4 FALSE x_2 
    CLEO_CALL PorcentagemDe 0 x_2 rel_x_size x_2

    //y_size
    i = p + UBERAPP_GUI_BACKGROUND_Y_SIZE
    READ_MEMORY i 4 FALSE y_2
    CLEO_CALL PorcentagemDe 0 y_2 rel_y_size y_2

    GET_LABEL_POINTER UberApp_Interface p 
    i = p + UBERAPP_GUI_BUTTOM_0_X_COORD 
    WRITE_MEMORY i 4 x_1 FALSE
    i = p + UBERAPP_GUI_BUTTOM_0_Y_COORD
    WRITE_MEMORY i 4 y_1 FALSE
    i = p + UBERAPP_GUI_BUTTOM_0_X_SIZE
    WRITE_MEMORY i 4 x_2 FALSE
    i = p + UBERAPP_GUI_BUTTOM_0_Y_SIZE
    WRITE_MEMORY i 4 y_2 FALSE
    
    /*
    i = p + UBERAPP_GUI_BUTTOM_0_RGBA_START
    WRITE_MEMORY i 1 0 FALSE //R
    i += 1
    WRITE_MEMORY i 1 200 FALSE //G
    i += 1
    WRITE_MEMORY i 1 0 FALSE //B
    i += 1
    WRITE_MEMORY i 1 255 FALSE //A
    */
    IF NOT sprite = 0
        GET_FIXED_XY_ASPECT_RATIO x_2 y_2 x_2 y_2
        DRAW_SPRITE sprite x_1 y_1 x_2 y_2 r g b a
    ELSE
        GET_FIXED_XY_ASPECT_RATIO x_2 y_2 x_2 y_2
        DRAW_RECT x_1 y_1 x_2 y_2 r g b a
    ENDIF
CLEO_RETURN 0
}

{//CLEO_CALL CriarBotaoEmBandeja 0 (0.0, -2.0) (80.0, 20.0)
CriarBotaoEmBandeja:
    LVAR_FLOAT dist_coord_x dist_coord_y
    LVAR_FLOAT rel_x_size rel_y_size
    LVAR_INT sprite r g b a 
    
    LVAR_INT p i n 
    LVAR_FLOAT x_1 y_1 x_2 y_2 x y

    GET_LABEL_POINTER UberApp_Interface p 

    //x_coord
    i = p + UBERAPP_GUI_TRAY_0_X_COORD
    READ_MEMORY i 4 FALSE x_1
    CLEO_CALL PorcentagemDe 0 x_1 dist_coord_x y
    y_1 = y_1 + y

    //y_coord
    i = p + UBERAPP_GUI_TRAY_0_Y_COORD
    READ_MEMORY i 4 FALSE y_1
    CLEO_CALL PorcentagemDe 0 y_1 dist_coord_y y
    y_1 = y_1 + y

    //x_size
    i = p + UBERAPP_GUI_TRAY_0_X_SIZE
    READ_MEMORY i 4 FALSE x_2 
    CLEO_CALL PorcentagemDe 0 x_2 rel_x_size x_2

    //y_size
    i = p + UBERAPP_GUI_TRAY_0_Y_SIZE
    READ_MEMORY i 4 FALSE y_2
    CLEO_CALL PorcentagemDe 0 y_2 rel_y_size y_2

    GET_LABEL_POINTER UberApp_Interface p 
    i = p + UBERAPP_GUI_BUTTOM_0_X_COORD 
    WRITE_MEMORY i 4 x_1 FALSE
    i = p + UBERAPP_GUI_BUTTOM_0_Y_COORD
    WRITE_MEMORY i 4 y_1 FALSE
    i = p + UBERAPP_GUI_BUTTOM_0_X_SIZE
    WRITE_MEMORY i 4 x_2 FALSE
    i = p + UBERAPP_GUI_BUTTOM_0_Y_SIZE
    WRITE_MEMORY i 4 y_2 FALSE
    
    /*
    i = p + UBERAPP_GUI_BUTTOM_0_RGBA_START
    WRITE_MEMORY i 1 0 FALSE //R
    i += 1
    WRITE_MEMORY i 1 200 FALSE //G
    i += 1
    WRITE_MEMORY i 1 0 FALSE //B
    i += 1
    WRITE_MEMORY i 1 255 FALSE //A
    */

    IF NOT sprite = 0
        GET_FIXED_XY_ASPECT_RATIO x_2 y_2 x_2 y_2
        DRAW_SPRITE sprite x_1 y_1 x_2 y_2 r g b a 
    ELSE
        GET_FIXED_XY_ASPECT_RATIO x_2 y_2 x_2 y_2
        DRAW_RECT x_1 y_1 x_2 y_2 r g b a 
    ENDIF
CLEO_RETURN 0
}

{//CLEO_CALL Active_Item_Cursor 0 CurrentAtiveItem (buttom_active_item_id)
Active_Item_Cursor: 

    LVAR_INT currentActiveItem
    LVAR_INT buttom_active_item_id

    LVAR_INT p i n
    LVAR_FLOAT x_1 y_1 x_2 y_2 x y

    IF currentActiveItem = buttom_active_item_id
        GET_LABEL_POINTER UberApp_Interface p 

        i = p + UBERAPP_GUI_BUTTOM_0_X_COORD 
        READ_MEMORY i 4 FALSE x_1

        i = p + UBERAPP_GUI_BUTTOM_0_Y_COORD
        READ_MEMORY i 4 FALSE y_1

        i = p + UBERAPP_GUI_BUTTOM_0_X_SIZE 
        READ_MEMORY i 4 FALSE x_2
        CLEO_CALL PorcentagemDe 0 x_2 2.0 x
        x_2 = x_2 + x
        i = p + UBERAPP_GUI_BUTTOM_0_Y_SIZE
        READ_MEMORY i 4 FALSE y_2
        CLEO_CALL PorcentagemDe 0 y_2 2.0 y
        y_2 = y_2 + y

        GET_FIXED_XY_ASPECT_RATIO x_2 y_2 x_2 y_2 
        DRAW_RECT x_1 y_1 x_2 y_2 0 0 200 100 
    ENDIF

CLEO_RETURN 0
}

{
PorcentagemDe:
    //in
    LVAR_FLOAT valor
    LVAR_FLOAT porcentagem

    valor *= porcentagem
    valor /= 100.0 

CLEO_RETURN 0 valor
}

{//CLEO_CALL UberApp_Calculator player 50.0 
UberApp_Calculator:
    LVAR_INT player 
    LVAR_FLOAT valor_km
    LVAR_FLOAT x y z x1 y1 z1
    LVAR_INT p i n

    GET_CHAR_COORDINATES player x1 y1 z1
    GET_TARGET_BLIP_COORDS x y z
    GET_DISTANCE_BETWEEN_COORDS_3D x1 y1 z1 x y z x1
    x1 /= 1000.0
    y1 = x1 * valor_km
    n =# y1
    

    GET_LABEL_POINTER UberApp_Class p 
    i = p + UBERAPP_DISTANCIA_DD
    WRITE_MEMORY i 4 x1 FALSE 
    i = p + UBERAPP_VALOR_POR_KM_DD 
    WRITE_MEMORY i 4 valor_km FALSE
    i = p + UBERAPP_VALOR_DA_CORRIDA_DD
    WRITE_MEMORY i 2 n FALSE
CLEO_RETURN 0
}


//OFFSETS
CONST_INT UBERAPP_DISTANCIA_DD 0
CONST_INT UBERAPP_VALOR_POR_KM_DD 4
CONST_INT UBERAPP_VALOR_DA_CORRIDA_DD 6
CONST_INT UBERAPP_VIAGEM_SOLICITADA 9
CONST_INT UBERAPP_VIAGEM_EM_ANDAMENTO 10
CONST_INT UBERAPP_VIAGEM_TERMINADA 11
CONST_INT UBERAPP_VALOR_DA_VIAGEM_SOLICITADA 13

CONST_INT UBERAPP_X 17 
CONST_INT UBERAPP_Y 21 
CONST_INT UBERAPP_Z 25 

UberApp_Class:
DUMP
00 00 00 00 //0 - Distancia
00 00 00 00 //4 - Valor por KM 
00 00 //6 - valor da corrida 
00 00 //8 

00 //9 - Viagem solicitada
00 //10 - Viagem em andamento
00 //11 - Viagem Terminada
00 00 //13 - Valor da corrida solicitada

00 00 00 00//17 - coodenada x
00 00 00 00//21 - coordenada y 
00 00 00 00//25 - coordenada z
ENDDUMP

{//CLEO_CALL GetRandomCoordsTowardsChar 0 (char) (from_float) (at_float) (return_x) (return_y) (return_z)
GetRandomCoordsTowardsChar: 
    //in
    LVAR_INT char
    LVAR_FLOAT fromFloat atFloat 

    LVAR_INT n n2
    LVAR_FLOAT x y z nFloat

    REPEAT 2 n

        GENERATE_RANDOM_FLOAT_IN_RANGE fromFloat atFloat nFloat

        GENERATE_RANDOM_INT_IN_RANGE 0 2 n2
        SWITCH n2 
            CASE 0
                nFloat *= -1.0
                BREAK
            CASE 1 
                nFloat *= 1.0 //atoa msm
                BREAK
        ENDSWITCH

        SWITCH n 
            CASE 0 //x
                x += nFloat
                BREAK
            CASE 1 //y
                y += nFloat
                BREAK
        ENDSWITCH
    ENDREPEAT

    GET_OFFSET_FROM_CHAR_IN_WORLD_COORDS char x y 0.0 x y z 
    GET_GROUND_Z_FOR_3D_COORD x y z z

CLEO_RETURN 0 (x, y, z)

}


{
    LVAR_INT memory // In
    LVAR_INT slot offset
    LVAR_FLOAT pos_x pos_y tam_x tam_y
    retornar_floats:

    memory += 34
    READ_MEMORY memory 4 FALSE (pos_x)
    memory += 4
    READ_MEMORY memory 4 FALSE (pos_y)
    memory += 4
    READ_MEMORY memory 4 FALSE (tam_x)
    memory += 4
    READ_MEMORY memory 4 FALSE (tam_y)

    //PRINT_FORMATTED_NOW "%f %f %f %f" 2000 pos_x pos_y tam_x tam_y

    CLEO_RETURN 0 (pos_x pos_y tam_x tam_y)
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
SCRIPT_END