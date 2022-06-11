SCRIPT_START
{
NOP
WAIT 10
/*Exemplos:
    Cria um background, seguindo ele que as outras funções se adaptariam ao tamanho

    //Agumentos
    1 - Sprite ID ou -1 para rect padrão
    2 e 3 - Coordenadas X e Y
    4 e 5 - Tamanho X e Y
    6 7 8 - R G B A Colors

    CLEO_CALL Draw_Background 0 -1 (570.0, 335.0) (75.0, 150.0) 200 0 0 255
    ___________________________________________________________________________________
    Cria um rect seguindo offsets do background setados

    //Agumentos
    1 - Sprite ID ou -1 para rect padrão
    2 e 3 - Coordenadas X e Y
    4 e 5 - Tamanho X e Y
    6 7 8 - R G B A Colors

    CLEO_CALL Draw_Rect_in_background 0 -1 (0.0, -3.0) (100.0, 20.0) 0 200 0 255
    ___________________________________________________________________________________
    Cria um text em background, ou rect, podendo ser setada no segunto argumento

    //Agumentos
    1 - ponteiro do buffer contendo a gxt
    2 - -1 para exibir somente texto, outros valores, para exibir textou ou outros valores...
    3 - Se ajusta no ultimo draw especificado, 0 = BACKGROUND, 1 = RECT

    4 e 5 - Coordenadas X e Y
    6 e 7 - Tamanho X e Y (são mais sensíveis)
    8 9 10 - R G B A Colors

    STRING_FORMAT p "UBE7" //Nome da entrada fxt
    CLEO_CALL Draw_Text_in_OtherDraw 0 p -1 1 (0.0, 0.0) (0.38 2.8) 255 255 255 255  
*/


LVAR_INT ponteiro ponteiro_label este_app
LVAR_FLOAT pos_x pos_y
//não altere valores das variáveis acima
LVAR_INT script player return_pointer var
LVAR_FLOAT float_flag x y z tam_x tam_y

//-------------------------------------------------------------------------------------

IF ponteiro = 0
    TERMINATE_THIS_CUSTOM_SCRIPT
ENDIF

LVAR_INT p i n item active_item
LVAR_TEXT_LABEL gxt
LVAR_FLOAT nfloat

GET_PLAYER_CHAR 0 player
LOAD_TEXTURE_DICTIONARY BankApp

LOAD_SPRITE 1 "background_bbc"
LOAD_SPRITE 2 "poupanca"
LOAD_SPRITE 3 "investir"
LOAD_SPRITE 4 "transferir"
LOAD_SPRITE 5 "emprestimo"
LOAD_SPRITE 6 "user"

CLEO_CALL retornar_floats 0 ponteiro_label (pos_x pos_y tam_x tam_y)
GET_FIXED_XY_ASPECT_RATIO tam_x tam_y tam_x tam_y

var = -999

WAIT 100

inicio:
    WAIT 0

    WHILE NOT var = este_app
        READ_MEMORY ponteiro 4 FALSE var
        WAIT 0
    ENDWHILE

    WHILE TRUE //NOT CLEO_CALL comandos 0 ponteiro_label (2)//VOLTAR
    //AND NOT IS_CHAR_DEAD player
        WAIT 0 //0x7b 0xa2 0x7b codigo de layers

        SET_TEXT_DRAW_BEFORE_FADE 1
        
        READ_MEMORY ponteiro 4 FALSE var
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

        //background
        CLEO_CALL retornar_floats 0 ponteiro_label (pos_x pos_y tam_x tam_y)
        CLEO_CALL Draw_Background 0 1 pos_x pos_y tam_x tam_y 255 255 255 255
            
            //Perfil do Personagem
                CLEO_CALL Draw_Rect_in_background 0 -1 (0.0, -21.0) (100.0, 10.0) 0x7b 0xa2 0x7b 255
                
                GET_LABEL_POINTER Buffer p
                STRING_FORMAT p "BBC0" //nome do carlos
                CLEO_CALL Draw_Text_in_OtherDraw 0 p -1 1 (1.1, -2.8) (0.35 6.0) 255 255 255 255
            
                STRING_FORMAT p "BBC1" //Cliente
                CLEO_CALL Draw_Text_in_OtherDraw 0 p -1 1 (-1.6, 0.62) (0.23 3.9) 255 255 255 200

                //foto
                CLEO_CALL Draw_SmallRect_in_rect 0 6 (-4.7, 0.0) (20.0 100.0) 255 255 255 255
                
                //SALDO BANCÁRIO
                //CLEO_CALL ReadGlobalVar 0 (419)(i) read global var in 0.2.7
                STORE_SCORE 0 i
                STRING_FORMAT p "BBC8"
                CLEO_CALL Draw_Text_in_OtherDraw 0 p i 0 (0.0, -17.0) (0.30 0.62) 255 255 255 255
                
            //Menu Principal Cabeçalho
                CLEO_CALL Draw_Rect_in_background 0 -1 (0.0, -11.0) (100.0, 9.0) 0x7b 0xa2 0x7b 255 

                GET_LABEL_POINTER Buffer p
                STRING_FORMAT p "BBC2" //Serviços
                CLEO_CALL Draw_Text_in_OtherDraw 0 p -1 1 (0.0, -2.0) (0.47 8.0) 255 255 255 255
            //Opções quadrados
            CONST_INT POUPANÇA_BUTTOM 0
            CONST_INT EMPRESTIMO_BUTTOM 1
            CONST_INT INVESTIR_BUTTOM 2
            CONST_INT TRANFERIR_BUTTOM 3
            REPEAT 4 item
                            
                SWITCH item
                    CASE POUPANÇA_BUTTOM
                        CLEO_CALL Draw_Rect_in_background 0 2 (-3.0, -1.0) (40.0 20.0) 255 255 255 255
                        CLEO_CALL Draw_SmallRect_in_rect 0 -1 (0.0, 3.5) (100.0 20.0) 0 0 0 190

                        GET_LABEL_POINTER Buffer p
                        STRING_FORMAT p "BBC3"
                        CLEO_CALL Draw_Text_in_OtherDraw 0 p -1 2 (0.0 -1.1) (0.8, 12.0) 255 255 255 255

                        n = POUPANÇA_BUTTOM
                        CLEO_CALL Active_Item_Cursor 0 active_item n 
                        IF active_item = n
                            IF CLEO_CALL comandos 0 ponteiro_label (1)//CONFIRMAR
                                WHILE NOT IS_CHAR_DEAD player
                                    WAIT 0

                                    
                                    CLEO_CALL retornar_floats 0 ponteiro_label (pos_x pos_y tam_x tam_y)
                                    CLEO_CALL Draw_Background 0 1 pos_x pos_y tam_x tam_y 255 255 255 255
                                    //Cabeçalho da poupança
                                    CLEO_CALL Draw_Rect_in_background 0 -1 (0.0, -20.0) (100.0 10.0) 0x7b 0xa2 0x7b 255
                                    
                                    GET_LABEL_POINTER Buffer p 
                                    STRING_FORMAT p "BBC3"
                                    CLEO_CALL Draw_Text_in_OtherDraw 0 p -1 1 (0.0, -1.75) (0.5, 6.5) 255 255 255 255

                                    //Descrição
                                    CLEO_CALL Draw_Rect_in_background 0 -1 (0.0, -10.0) (100.0 28.0) 0 0 0 190
                                    STRING_FORMAT p "BBC11"
                                    CLEO_CALL Draw_Text_in_OtherDraw 0 p -1 1 (0.0, -5.7) (0.248, 2.1) 255 255 255 255
                                    STRING_FORMAT p "BBC12"
                                    CLEO_CALL Draw_Text_in_OtherDraw 0 p -1 1 (-0.2, -3.1) (0.248, 2.1) 255 255 255 255                                        
                                    STRING_FORMAT p "BBC13"
                                    CLEO_CALL Draw_Text_in_OtherDraw 0 p -1 1 (-4.8, -0.3) (0.248, 2.1) 255 255 255 255

                                    //Area do valor
                                    CLEO_CALL Draw_Rect_in_background 0 -1 (0.0, -3.0) (100.0 10.0) 0x7b 0xa2 0x7b 255
                                    STRING_FORMAT p "BBC14"
                                    CLEO_CALL Draw_Text_in_OtherDraw 0 p -1 1 (0.0, -1.75) (0.5, 6.5) 255 255 255 255

                                    CLEO_CALL ReadGlobalVar 0 (420)(i)
                                    i = 0
                                    STRING_FORMAT p "BBC15"
                                    CLEO_CALL Draw_Text_in_OtherDraw 0 p i 0 (0.0, 0.0) (0.45, 1.0) 255 255 255 255
                                    
                                    //Rendimento
                                    CLEO_CALL Draw_Rect_in_background 0 -1 (0.0, 7.5) (100.0 10.0) 0x7b 0xa2 0x7b 255
                                    STRING_FORMAT p "BBC16"
                                    CLEO_CALL Draw_Text_in_OtherDraw 0 p -1 1 (0.0, -1.83) (0.46, 7.0) 255 255 255 255
                                    STRING_FORMAT p "BBC17"
                                    CLEO_CALL Draw_Text_in_OtherDraw 0 p 7 0 (0.0, 11.0) (0.63, 1.49) 255 255 255 255

                                    STRING_FORMAT p "BBC18"
                                    CLEO_CALL Draw_Text_in_OtherDraw 0 p -1 0 (0.0, 18.0) (0.3, 0.6) 255 255 255 255

                                    USE_TEXT_COMMANDS 0

                                    IF CLEO_CALL comandos 0 ponteiro_label (2)//VOLTAR
                                        nfloat = 0.0
                                        WHILE CLEO_CALL comandos 0 ponteiro_label (2)//VOLTAR
                                            WAIT 0 
                                            CLEO_CALL retornar_floats 0 ponteiro_label (pos_x pos_y tam_x tam_y)
                                            CLEO_CALL Draw_Background 0 1 pos_x pos_y tam_x tam_y 255 255 255 255
                                            nfloat +=@ 1.0
                                            IF nfloat >= 40.0
                                                BREAK
                                            ENDIF
                                        ENDWHILE
                                        BREAK
                                    ENDIF
                                ENDWHILE

                            ENDIF
                        ENDIF

                        BREAK
                    CASE EMPRESTIMO_BUTTOM
                        CLEO_CALL Draw_Rect_in_background 0 5 (3.0, -1.0) (40.0 20.0) 255 255 255 255
                        CLEO_CALL Draw_SmallRect_in_rect 0 -1 (0.0, 3.5) (100.0 20.0) 0 0 0 190

                        GET_LABEL_POINTER Buffer p
                        STRING_FORMAT p "BBC4" 
                        CLEO_CALL Draw_Text_in_OtherDraw 0 p -1 2 (0.0 -1.1) (0.6, 11.7) 255 255 255 255

                        n = EMPRESTIMO_BUTTOM
                        CLEO_CALL Active_Item_Cursor 0 active_item n 
                        IF active_item = n
                            IF CLEO_CALL comandos 0 ponteiro_label (1)//CONFIRMAR
                                WHILE NOT IS_CHAR_DEAD player
                                    WAIT 0
                                    
                                    GOSUB BandFunction

                                    USE_TEXT_COMMANDS 0
                                    IF CLEO_CALL comandos 0 ponteiro_label (2)//VOLTAR
                                        nfloat = 0.0
                                        WHILE CLEO_CALL comandos 0 ponteiro_label (2)//VOLTAR
                                            WAIT 0 
                                            CLEO_CALL retornar_floats 0 ponteiro_label (pos_x pos_y tam_x tam_y)
                                            CLEO_CALL Draw_Background 0 1 pos_x pos_y tam_x tam_y 255 255 255 255
                                            nfloat +=@ 1.0
                                            IF nfloat >= 40.0
                                                BREAK
                                            ENDIF
                                        ENDWHILE
                                        BREAK
                                    ENDIF

                                ENDWHILE


                            ENDIF
                        ENDIF

                        BREAK
                    CASE INVESTIR_BUTTOM
                        CLEO_CALL Draw_Rect_in_background 0 3 (-3.0, 10.5) (40.0 20.0) 255 255 255 255
                        CLEO_CALL Draw_SmallRect_in_rect 0 -1 (0.0, 3.5) (100.0 20.0) 0 0 0 190

                        GET_LABEL_POINTER Buffer p
                        STRING_FORMAT p "BBC5" //nome do carlos
                        CLEO_CALL Draw_Text_in_OtherDraw 0 p -1 2 (0.0 -1.1) (0.8, 12.0) 255 255 255 255

                        n = INVESTIR_BUTTOM
                        CLEO_CALL Active_Item_Cursor 0 active_item n 
                        IF active_item = n
                            IF CLEO_CALL comandos 0 ponteiro_label (1)//CONFIRMAR
                                WHILE NOT IS_CHAR_DEAD player
                                    WAIT 0
                                    
                                    GOSUB BandFunction

                                    USE_TEXT_COMMANDS 0
                                    
                                    IF CLEO_CALL comandos 0 ponteiro_label (2)//VOLTAR
                                        nfloat = 0.0
                                        WHILE CLEO_CALL comandos 0 ponteiro_label (2)//VOLTAR
                                            WAIT 0 
                                            CLEO_CALL retornar_floats 0 ponteiro_label (pos_x pos_y tam_x tam_y)
                                            CLEO_CALL Draw_Background 0 1 pos_x pos_y tam_x tam_y 255 255 255 255
                                            nfloat +=@ 1.0
                                            IF nfloat >= 40.0
                                                BREAK
                                            ENDIF
                                        ENDWHILE
                                        BREAK
                                    ENDIF
                                ENDWHILE
                            ENDIF
                        ENDIF
                        BREAK
                    CASE TRANFERIR_BUTTOM
                        CLEO_CALL Draw_Rect_in_background 0 4 (3.0, 10.5) (40.0 20.0) 255 255 255 255
                        CLEO_CALL Draw_SmallRect_in_rect 0 -1 (0.0, 3.5) (100.0 20.0) 0 0 0 190

                        GET_LABEL_POINTER Buffer p
                        STRING_FORMAT p "BBC6" //nome do carlos
                        CLEO_CALL Draw_Text_in_OtherDraw 0 p -1 2 (0.0 -1.1) (0.8, 12.0) 255 255 255 255
                        n = TRANFERIR_BUTTOM
                        CLEO_CALL Active_Item_Cursor 0 active_item n 
                        IF active_item = n
                            IF CLEO_CALL comandos 0 ponteiro_label (1)//CONFIRMAR
                                WHILE NOT IS_CHAR_DEAD player
                                    WAIT 0
                                    
                                    GOSUB BandFunction

                                    USE_TEXT_COMMANDS 0
                                    
                                    IF CLEO_CALL comandos 0 ponteiro_label (2)//VOLTAR
                                        nfloat = 0.0
                                        WHILE CLEO_CALL comandos 0 ponteiro_label (2)//VOLTAR
                                            WAIT 0 
                                            CLEO_CALL retornar_floats 0 ponteiro_label (pos_x pos_y tam_x tam_y)
                                            CLEO_CALL Draw_Background 0 1 pos_x pos_y tam_x tam_y 255 255 255 255
                                            nfloat +=@ 1.0
                                            IF nfloat >= 40.0
                                                BREAK
                                            ENDIF
                                        ENDWHILE
                                        BREAK
                                    ENDIF
                                ENDWHILE
                            ENDIF
                            /*0.2.7
                            nfloat = 0.0
                            WHILE CLEO_CALL comandos 0 ponteiro_label (1)//CONFIRMAR
                                WAIT 0 
                                CLEO_CALL retornar_floats 0 ponteiro_label (pos_x pos_y tam_x tam_y)
                                CLEO_CALL Draw_Background 0 1 pos_x pos_y tam_x tam_y 255 255 255 255
                                nfloat +=@ 1.0
                                IF nfloat >= 40.0
                                    BREAK
                                ENDIF
                            ENDWHILE

                            WHILE NOT IS_CHAR_DEAD player
                                WAIT 0
                                
                                CLEO_CALL retornar_floats 0 ponteiro_label (pos_x pos_y tam_x tam_y)
                                CLEO_CALL Draw_Background 0 1 pos_x pos_y tam_x tam_y 255 255 255 255  
                
                                //Cabeçalho das transferencias
                                CLEO_CALL Draw_Rect_in_background 0 -1 (0.0, -20.0) (100.0 10.0) 0x7b 0xa2 0x7b 255
                                
                                GET_LABEL_POINTER Buffer p 
                                STRING_FORMAT p "BBC6"
                                CLEO_CALL Draw_Text_in_OtherDraw 0 p -1 1 (0.0, -1.75) (0.5, 6.5) 255 255 255 255

                                //Descrição
                                CLEO_CALL Draw_Rect_in_background 0 -1 (0.0, -10.0) (100.0 28.0) 0 0 0 190
                                STRING_FORMAT p "BBC20"
                                CLEO_CALL Draw_Text_in_OtherDraw 0 p -1 1 (0.0, -5.7) (0.248, 2.1) 255 255 255 255
                                STRING_FORMAT p "BBC21"
                                CLEO_CALL Draw_Text_in_OtherDraw 0 p -1 1 (-0.8, -3.1) (0.248, 2.1) 255 255 255 255                                        
                                STRING_FORMAT p "BBC22"
                                CLEO_CALL Draw_Text_in_OtherDraw 0 p -1 1 (-2.14, -0.3) (0.248, 2.1) 255 255 255 255

                                CLEO_CALL Draw_Rect_in_background 0 -1 (0.0 3.0) (75.0 10.0) 0x7b 0xa2 0x7b 255
                                STRING_FORMAT p "BBC19"
                                CLEO_CALL Draw_Text_in_OtherDraw 0 p -1 1 (0.0, -1.75) (0.5, 6.5) 255 255 255 255
                                
                                CONST_INT PAGAMENTO_BUTTOM 0
                                n = PAGAMENTO_BUTTOM
                                active_item = PAGAMENTO_BUTTOM
                                CLEO_CALL Active_Item_Cursor 0 active_item n 
                                IF active_item = n
                                    IF CLEO_CALL comandos 0 ponteiro_label (1)//CONFIRMAR

                                        nfloat = 0.0
                                        WHILE CLEO_CALL comandos 0 ponteiro_label (1)//CONFIRMAR
                                            WAIT 0 
                                            CLEO_CALL retornar_floats 0 ponteiro_label (pos_x pos_y tam_x tam_y)
                                            CLEO_CALL Draw_Background 0 1 pos_x pos_y tam_x tam_y 255 255 255 255
                                            nfloat +=@ 1.0
                                            IF nfloat >= 40.0
                                                BREAK
                                            ENDIF
                                        ENDWHILE

                                        WHILE NOT IS_CHAR_DEAD player
                                            WAIT 0 

                                            CLEO_CALL retornar_floats 0 ponteiro_label (pos_x pos_y tam_x tam_y)
                                            CLEO_CALL Draw_Background 0 1 pos_x pos_y tam_x tam_y 255 255 255 255 

                                            //Cabeçalho de Pagamento
                                            CLEO_CALL Draw_Rect_in_background 0 -1 (0.0, -20.0) (100.0 10.0) 0x7b 0xa2 0x7b 255
                                            
                                            GET_LABEL_POINTER Buffer p 
                                            STRING_FORMAT p "BBC19"
                                            CLEO_CALL Draw_Text_in_OtherDraw 0 p -1 1 (0.0, -1.75) (0.5, 6.5) 255 255 255 255

                                            SWITCH n
                                                CASE 0 //PAGAMENTO VÁLIDO
                                                    CLEO_CALL Draw_Rect_in_background 0 -1 (0.0 2.5) (100.0, 70.0) 0 0 0 190

                                                    STRING_FORMAT p "BBC23"
                                                    CLEO_CALL Draw_Text_in_OtherDraw 0 p -1 1 (0.0, -13.0) (0.3, 1.0) 255 255 255 255
                                                    CLEO_CALL Draw_SmallRect_in_rect 0 -1 (0.0 -7.0) (80.0 10.0) 255 255 255 190

                                                    STRING_FORMAT p "BBC24"
                                                    CLEO_CALL Draw_Text_in_OtherDraw 0 p -1 1 (0.0, -3.0) (0.3, 1.0) 255 255 255 255
                                                    CLEO_CALL Draw_SmallRect_in_rect 0 -1 (0.0 3.0) (80.0 10.0) 255 255 255 190

                                                    CLEO_CALL Draw_Rect_in_background 0 -1 (0.0 15.0) (75.0 10.0) 0x7b 0xa2 0x7b 255
                                                    STRING_FORMAT p "BBC25"
                                                    CLEO_CALL Draw_Text_in_OtherDraw 0 p -1 1 (0.0, -1.65) (0.5, 6.5) 255 255 255 255

                                                    BREAK
                                            ENDSWITCH

                                            USE_TEXT_COMMANDS 0
                                            
                                            IF CLEO_CALL comandos 0 ponteiro_label (2)//VOLTAR
                                                nfloat = 0.0
                                                WHILE CLEO_CALL comandos 0 ponteiro_label (2)//VOLTAR
                                                    WAIT 0 
                                                    CLEO_CALL retornar_floats 0 ponteiro_label (pos_x pos_y tam_x tam_y)
                                                    CLEO_CALL Draw_Background 0 1 pos_x pos_y tam_x tam_y 255 255 255 255
                                                    nfloat +=@ 1.0
                                                    IF nfloat >= 40.0
                                                        BREAK
                                                    ENDIF
                                                ENDWHILE
                                                BREAK
                                            ENDIF
                                        ENDWHILE          


                                    ENDIF
                                ENDIF

                                USE_TEXT_COMMANDS 0

                                IF CLEO_CALL comandos 0 ponteiro_label (2)//VOLTAR
                                    nfloat = 0.0
                                    WHILE CLEO_CALL comandos 0 ponteiro_label (2)//VOLTAR
                                        WAIT 0 
                                        CLEO_CALL retornar_floats 0 ponteiro_label (pos_x pos_y tam_x tam_y)
                                        CLEO_CALL Draw_Background 0 1 pos_x pos_y tam_x tam_y 255 255 255 255
                                        nfloat +=@ 1.0
                                        IF nfloat >= 40.0
                                            BREAK
                                        ENDIF
                                    ENDWHILE
                                    BREAK
                                ENDIF
                            ENDWHILE
                            */
                        ENDIF
                        BREAK
                ENDSWITCH
                            
                IF CLEO_CALL comandos 0 ponteiro_label 4//cima
                    IF timera >= 200
                        IF active_item = 0
                            active_item = 2
                        ELSE
                            IF active_item = 2
                                active_item = 0
                            ENDIF
                        ENDIF

                        IF active_item = 1
                            active_item = 3
                        ELSE
                            IF active_item = 3
                                active_item = 1
                            ENDIF
                        ENDIF

                        timera = 0
                    ENDIF
                ENDIF

                IF CLEO_CALL comandos 0 ponteiro_label 5//baixo
                    IF timera >= 200
                        IF active_item = 0
                            active_item = 2
                        ELSE
                            IF active_item = 2
                                active_item = 0
                            ENDIF
                        ENDIF

                        IF active_item = 1
                            active_item = 3
                        ELSE
                            IF active_item = 3
                                active_item = 1
                            ENDIF
                        ENDIF

                        timera = 0
                    ENDIF
                ENDIF
                
                IF CLEO_CALL comandos 0 ponteiro_label 7//direita
                    IF timera >= 200
                        IF active_item = 0
                            active_item = 1
                        ELSE
                            IF active_item = 1
                                active_item = 0
                            ENDIF
                        ENDIF

                        IF active_item = 2
                            active_item = 3
                        ELSE
                            IF active_item = 3
                                active_item = 2
                            ENDIF
                        ENDIF

                        timera = 0
                    ENDIF
                ENDIF

                IF CLEO_CALL comandos 0 ponteiro_label 6//esquerda
                    IF timera >= 200
                        IF active_item = 0
                            active_item = 1
                        ELSE
                            IF active_item = 1
                                active_item = 0
                            ENDIF
                        ENDIF

                        IF active_item = 2
                            active_item = 3
                        ELSE
                            IF active_item = 3
                                active_item = 2
                            ENDIF
                        ENDIF

                        timera = 0
                    ENDIF
                ENDIF

            ENDREPEAT
            //Desenho inferior
            CLEO_CALL Draw_Rect_in_background 0 -1 (0.0, 22.0) (100.0, 15.0) 0x7b 0xa2 0x7b 255 
            
            GET_LABEL_POINTER Buffer p
            STRING_FORMAT p "BBC7" //Serviços
            CLEO_CALL Draw_Text_in_OtherDraw 0 p -1 1 (0.0, -2.0) (0.32 5.3) 0 0 0 255
        
        USE_TEXT_COMMANDS 1
    ENDWHILE
GOTO inicio

check:
READ_MEMORY ponteiro 4 FALSE var
IF var = este_app
    RETURN_FALSE
    RETURN
ELSE
    RETURN_TRUE
    RETURN
ENDIF

BandFunction: 
    //Background página de poupança
    CLEO_CALL retornar_floats 0 ponteiro_label (pos_x pos_y tam_x tam_y)
    //GET_FIXED_XY_ASPECT_RATIO tam_x tam_y tam_x tam_y
    CLEO_CALL Draw_Background 0 1 pos_x pos_y tam_x tam_y 255 255 255 255
    GET_LABEL_POINTER Buffer p 
    STRING_FORMAT p "BBC10"
    CLEO_CALL Draw_Text_in_OtherDraw 0 p -1 0 (0.0, 0.0) (0.22, 0.70) 255 255 255 255
RETURN
//GET_FIXED_XY_ASPECT_RATIO tam_x tam_y tam_x tam_y
}



{//CLEO_CALL Active_Item_Cursor 0 CurrentAtiveItem (buttom_active_item_id)
Active_Item_Cursor: 

    LVAR_INT currentActiveItem
    LVAR_INT buttom_active_item_id

    LVAR_INT p i n
    LVAR_FLOAT x_1 y_1 x_2 y_2 x y

    IF currentActiveItem = buttom_active_item_id
        GET_LABEL_POINTER AppInterface_label p 

        i = p + APP_INTERFACE_RECT_X_COORD
        READ_MEMORY i 4 FALSE x_1

        i = p + APP_INTERFACE_RECT_Y_COORD
        READ_MEMORY i 4 FALSE y_1

        i = p + APP_INTERFACE_RECT_X_SIZE
        READ_MEMORY i 4 FALSE x_2
        CLEO_CALL PorcentagemDe 0 x_2 2.0 x
        x_2 = x_2 + x
        i = p + APP_INTERFACE_RECT_Y_SIZE
        READ_MEMORY i 4 FALSE y_2
        CLEO_CALL PorcentagemDe 0 y_2 2.0 y
        y_2 = y_2 + y

        DRAW_RECT x_1 y_1 x_2 y_2 0 0 200 100 
    ENDIF

CLEO_RETURN 0
}

{
Draw_Background:
    LVAR_INT sprite
    LVAR_FLOAT x_1 y_1
    LVAR_FLOAT x_2 y_2
    LVAR_INT r g b a

    LVAR_FLOAT nfloat x_top x_down y_left y_right

    LVAR_INT p i n 
    
    GET_LABEL_POINTER AppInterface_label p
    i = p + APP_INTERFACE_BACKGROUND_X_COORD
    WRITE_MEMORY i 4 x_1 FALSE
    i = p + APP_INTERFACE_BACKGROUND_Y_COORD
    WRITE_MEMORY i 4 y_1 FALSE

    GET_FIXED_XY_ASPECT_RATIO x_2 y_2 x_2 y_2

    i = p + APP_INTERFACE_BACKGROUND_X_SIZE
    WRITE_MEMORY i 4 x_2 FALSE
    i = p + APP_INTERFACE_BACKGROUND_Y_SIZE
    WRITE_MEMORY i 4 y_2 FALSE


    //top
    nfloat = x_2 / 2.0
    x_top = x_1 - nfloat
    //down
    x_down = x_1 + nfloat

    nfloat = y_2 / 2.0
    y_left = y_1 - nfloat
    y_right = y_1 + nfloat
    

    IF NOT sprite = -1
        DRAW_SPRITE sprite x_1 y_1 x_2 y_2 r g b a
    ELSE
        DRAW_RECT x_1 y_1 x_2 y_2 r g b a
    ENDIF
CLEO_RETURN 0
}

{
Draw_Rect_in_background:
    LVAR_INT sprite
    LVAR_FLOAT pct_x_coord pct_y_coord
    LVAR_FLOAT pct_x_size pct_y_size
    LVAR_INT r g b a

    LVAR_INT p i n 
    LVAR_FLOAT x_1 y_1 x_2 y_2 nfloat
    
    GET_LABEL_POINTER AppInterface_label p 
    i = p + APP_INTERFACE_BACKGROUND_X_COORD 
    READ_MEMORY i 4 FALSE x_1
    i = p + APP_INTERFACE_BACKGROUND_Y_COORD
    READ_MEMORY i 4 FALSE y_1

    CLEO_CALL PorcentagemDe 0 x_1 pct_x_coord nfloat
    x_1 = x_1 + nfloat

    CLEO_CALL PorcentagemDe 0 y_1 pct_y_coord nfloat
    y_1 = y_1 + nfloat
    
    i = p + APP_INTERFACE_BACKGROUND_X_SIZE
    READ_MEMORY i 4 FALSE x_2
    i = p + APP_INTERFACE_BACKGROUND_Y_SIZE
    READ_MEMORY i 4 FALSE y_2

    CLEO_CALL PorcentagemDe 0 x_2 pct_x_size x_2
    CLEO_CALL PorcentagemDe 0 y_2 pct_y_size y_2

    

    i = p + APP_INTERFACE_RECT_X_COORD
    WRITE_MEMORY i 4 x_1 FALSE
    i = p + APP_INTERFACE_RECT_Y_COORD
    WRITE_MEMORY i 4 y_1 FALSE

    i = p + APP_INTERFACE_RECT_X_SIZE
    WRITE_MEMORY i 4 x_2 FALSE
    i = p + APP_INTERFACE_RECT_Y_SIZE
    WRITE_MEMORY i 4 y_2 FALSE 

    IF NOT sprite = -1
        DRAW_SPRITE sprite x_1 y_1 x_2 y_2 r g b a
    ELSE
        DRAW_RECT x_1 y_1 x_2 y_2 r g b a
    ENDIF

CLEO_RETURN 0
}

{
Draw_SmallRect_in_rect:
    LVAR_INT sprite
    LVAR_FLOAT pct_x_coord pct_y_coord
    LVAR_FLOAT pct_x_size pct_y_size
    LVAR_INT r g b a

    LVAR_INT p i n 
    LVAR_FLOAT x_1 y_1 x_2 y_2 nfloat
    
    GET_LABEL_POINTER AppInterface_label p 
    i = p + APP_INTERFACE_RECT_X_COORD 
    READ_MEMORY i 4 FALSE x_1
    i = p + APP_INTERFACE_RECT_Y_COORD
    READ_MEMORY i 4 FALSE y_1

    CLEO_CALL PorcentagemDe 0 x_1 pct_x_coord nfloat
    x_1 = x_1 + nfloat

    CLEO_CALL PorcentagemDe 0 y_1 pct_y_coord nfloat
    y_1 = y_1 + nfloat
    
    i = p + APP_INTERFACE_RECT_X_SIZE
    READ_MEMORY i 4 FALSE x_2
    i = p + APP_INTERFACE_RECT_Y_SIZE
    READ_MEMORY i 4 FALSE y_2

    CLEO_CALL PorcentagemDe 0 x_2 pct_x_size x_2
    CLEO_CALL PorcentagemDe 0 y_2 pct_y_size y_2

    i = p + APP_INTERFACE_SMALL_RECT_X_COORD
    WRITE_MEMORY i 4 x_1 FALSE
    i = p + APP_INTERFACE_SMALL_RECT_Y_COORD
    WRITE_MEMORY i 4 y_1 FALSE

    i = p + APP_INTERFACE_SMALL_RECT_X_SIZE
    WRITE_MEMORY i 4 x_2 FALSE
    i = p + APP_INTERFACE_SMALL_RECT_Y_SIZE
    WRITE_MEMORY i 4 y_2 FALSE 

    IF NOT sprite = -1
        DRAW_SPRITE sprite x_1 y_1 x_2 y_2 r g b a
    ELSE
        DRAW_RECT x_1 y_1 x_2 y_2 r g b a
    ENDIF

CLEO_RETURN 0
}

{
Draw_Text_in_OtherDraw:
    LVAR_INT pText
    LVAR_INT Number
    LVAR_INT draw
    LVAR_FLOAT pct_x_coord pct_y_coord
    LVAR_FLOAT pct_x_size pct_y_size
    LVAR_INT r g b a


    LVAR_INT p i n s 
    LVAR_FLOAT x_1 y_1 x_2 y_2 nfloat
    LVAR_TEXT_LABEL gxt

    CONST_INT BACKGROUND 0
    CONST_INT RECT 1
    CONST_INT SMALL_RECT 2

    GET_LABEL_POINTER AppInterface_label p 

    SWITCH draw
        CASE BACKGROUND
            i = p + APP_INTERFACE_BACKGROUND_X_COORD 
            READ_MEMORY i 4 FALSE x_1
            i = p + APP_INTERFACE_BACKGROUND_Y_COORD
            READ_MEMORY i 4 FALSE y_1

            i = p + APP_INTERFACE_BACKGROUND_X_SIZE
            READ_MEMORY i 4 FALSE x_2
            i = p + APP_INTERFACE_BACKGROUND_Y_SIZE
            READ_MEMORY i 4 FALSE y_2
            BREAK
        CASE RECT
            i = p + APP_INTERFACE_RECT_X_COORD 
            READ_MEMORY i 4 FALSE x_1
            i = p + APP_INTERFACE_RECT_Y_COORD
            READ_MEMORY i 4 FALSE y_1

            i = p + APP_INTERFACE_RECT_X_SIZE
            READ_MEMORY i 4 FALSE x_2
            i = p + APP_INTERFACE_RECT_Y_SIZE
            READ_MEMORY i 4 FALSE y_2
            BREAK
        CASE SMALL_RECT
            i = p + APP_INTERFACE_SMALL_RECT_X_COORD 
            READ_MEMORY i 4 FALSE x_1
            i = p + APP_INTERFACE_SMALL_RECT_Y_COORD
            READ_MEMORY i 4 FALSE y_1

            i = p + APP_INTERFACE_SMALL_RECT_X_SIZE
            READ_MEMORY i 4 FALSE x_2
            i = p + APP_INTERFACE_SMALL_RECT_Y_SIZE
            READ_MEMORY i 4 FALSE y_2
            BREAK
    ENDSWITCH

    CLEO_CALL PorcentagemDe 0 x_1 pct_x_coord nfloat
    x_1 = x_1 + nfloat
    CLEO_CALL PorcentagemDe 0 y_1 pct_y_coord nfloat
    y_1 = y_1 + nfloat

    CLEO_CALL PorcentagemDe 0 x_2 pct_x_size x_2
    CLEO_CALL PorcentagemDe 0 y_2 pct_y_size y_2

    GET_FIXED_XY_ASPECT_RATIO x_2 y_2 x_2 y_2

    SET_TEXT_CENTRE_SIZE 640.0 
    SET_TEXT_COLOUR r g b a
    SET_TEXT_SCALE x_2 y_2
    SET_TEXT_CENTRE 1
    SET_TEXT_DROPSHADOW 0 0 0 0 0

    STRING_FORMAT gxt $pText
    IF Number = -1
        DISPLAY_TEXT x_1 y_1 $gxt
    ELSE
        DISPLAY_TEXT_WITH_NUMBER x_1 y_1 $gxt Number
    ENDIF

CLEO_RETURN 0
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
PorcentagemDe:
    //in
    LVAR_FLOAT valor
    LVAR_FLOAT porcentagem

    valor *= porcentagem
    valor /= 100.0 

CLEO_RETURN 0 valor
}

{
    LVAR_INT var value //In
    LVAR_INT scriptSpace finalOffset

    WriteGlobalVar:
    READ_MEMORY 0x00468D5E 4 1 (scriptSpace)
    finalOffset = var * 4
    finalOffset += scriptSpace
    WRITE_MEMORY finalOffset 4 (value) FALSE
    CLEO_RETURN 0 ()
}

{
    LVAR_INT var //In
    LVAR_INT value scriptSpace finalOffset

    ReadGlobalVar:
    READ_MEMORY 0x00468D5E 4 1 (scriptSpace)
    finalOffset = var * 4
    finalOffset += scriptSpace
    READ_MEMORY finalOffset 4 FALSE (value)
    CLEO_RETURN 0 (value)
}

CONST_INT APP_INTERFACE_BACKGROUND_X_COORD 0
CONST_INT APP_INTERFACE_BACKGROUND_Y_COORD 4
CONST_INT APP_INTERFACE_BACKGROUND_X_SIZE 8 
CONST_INT APP_INTERFACE_BACKGROUND_Y_SIZE 12

CONST_INT APP_INTERFACE_RECT_X_COORD 16
CONST_INT APP_INTERFACE_RECT_Y_COORD 20
CONST_INT APP_INTERFACE_RECT_X_SIZE 24
CONST_INT APP_INTERFACE_RECT_Y_SIZE 28

CONST_INT APP_INTERFACE_TEXT_X_COORD 32
CONST_INT APP_INTERFACE_TEXT_Y_COORD 36
CONST_INT APP_INTERFACE_TEXT_X_SIZE 40
CONST_INT APP_INTERFACE_TEXT_Y_SIZE 44

CONST_INT APP_INTERFACE_SMALL_RECT_X_COORD 48
CONST_INT APP_INTERFACE_SMALL_RECT_Y_COORD 52
CONST_INT APP_INTERFACE_SMALL_RECT_X_SIZE 56
CONST_INT APP_INTERFACE_SMALL_RECT_Y_SIZE 60

CONST_INT APP_INTERFACE_SELECT_BUTTOM 61 
CONST_INT APP_INTERFACE_BACK_BUTTOM 62

AppInterface_label:
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

00 
00 

ENDDUMP

Buffer:
DUMP
00 00 00 00 00 00
ENDDUMP

SCRIPT_END