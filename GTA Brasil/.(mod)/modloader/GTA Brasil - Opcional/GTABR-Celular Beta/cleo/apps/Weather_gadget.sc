SCRIPT_START
{
LVAR_INT ponteiro ponteiro_label este_app
LVAR_FLOAT pos_x pos_y
LVAR_FLOAT tam_x tam_y
LVAR_INT var

LVAR_INT curWeather nextWeather sprite hour min weather night_start_hour iniDrawShadow usingMobileHud
LVAR_FLOAT posX posY sizX sizY iniPosX iniPosY iniSizX iniSizY


IF ponteiro = 0
    TERMINATE_THIS_CUSTOM_SCRIPT
ENDIF

GET_FIXED_XY_ASPECT_RATIO (25.0 25.0) (tam_x tam_y)

// Sprites const
CONST_INT SPRITE_FOG                   1
CONST_INT SPRITE_NIGHT                 2
CONST_INT SPRITE_NIGHT_PARTLY_CLOUDY   3        
CONST_INT SPRITE_NIGHT_RAIN            4
CONST_INT SPRITE_CLOUDY                5
CONST_INT SPRITE_CLOUDY_NIGHT          6
CONST_INT SPRITE_CLOUDY_S_RAIN         7
CONST_INT SPRITE_CLOUDY_S_SUNNY        8
CONST_INT SPRITE_RAIN                  9
CONST_INT SPRITE_RAIN_NIGHT           10
CONST_INT SPRITE_RAIN_S_CLOUDY        11
CONST_INT SPRITE_RAIN_S_SUNNY         12
CONST_INT SPRITE_THUNDERSTORMS        13
CONST_INT SPRITE_SUNNY                14
CONST_INT SPRITE_PARTLY_CLOUD         15
CONST_INT SPRITE_SUNNY_S_CLOUDY       16
CONST_INT SPRITE_SUNNY_S_RAIN         17
CONST_INT SPRITE_WINDY                18

// Load sprites
LOAD_TEXTURE_DICTIONARY WEAFORE

LOAD_SPRITE SPRITE_NIGHT               night
LOAD_SPRITE SPRITE_NIGHT_PARTLY_CLOUDY night_p_cldy
LOAD_SPRITE SPRITE_NIGHT_RAIN          night_rain

LOAD_SPRITE SPRITE_CLOUDY              cloudy
LOAD_SPRITE SPRITE_CLOUDY_NIGHT        cloudy_night
LOAD_SPRITE SPRITE_CLOUDY_S_RAIN       cloudy_s_rain
LOAD_SPRITE SPRITE_CLOUDY_S_SUNNY      cloudy_s_sunny

LOAD_SPRITE SPRITE_RAIN                rain
LOAD_SPRITE SPRITE_RAIN_NIGHT          rain_night
LOAD_SPRITE SPRITE_RAIN_S_CLOUDY       rain_s_cloudy
LOAD_SPRITE SPRITE_RAIN_S_SUNNY        rain_s_sunny

LOAD_SPRITE SPRITE_THUNDERSTORMS       thunderstorms
LOAD_SPRITE SPRITE_WINDY               windy
LOAD_SPRITE SPRITE_FOG                 fog

LOAD_SPRITE SPRITE_SUNNY               sunny
LOAD_SPRITE SPRITE_PARTLY_CLOUD        partly_cloudy
LOAD_SPRITE SPRITE_SUNNY_S_CLOUDY      sunny_s_cloudy
LOAD_SPRITE SPRITE_SUNNY_S_RAIN        sunny_s_rain

// Weathers const
CONST_INT EXTRASUNNY_LA           0 
CONST_INT SUNNY_LA                1 
CONST_INT EXTRASUNNY_SMOG_LA      2 
CONST_INT SUNNY_SMOG_LA           3 
CONST_INT CLOUDY_LA               4 
CONST_INT SUNNY_SF                5 
CONST_INT EXTRASUNNY_SF           6 
CONST_INT CLOUDY_SF               7 
CONST_INT RAINY_SF                8 
CONST_INT FOGGY_SF                9 
CONST_INT SUNNY_VEGAS            10
CONST_INT EXTRASUNNY_VEGAS       11
CONST_INT CLOUDY_VEGAS           12
CONST_INT EXTRASUNNY_COUNTRYSIDE 13
CONST_INT SUNNY_COUNTRYSIDE      14
CONST_INT CLOUDY_COUNTRYSIDE     15
CONST_INT RAINY_COUNTRYSIDE      16
CONST_INT EXTRASUNNY_DESERT      17
CONST_INT SUNNY_DESERT           18
CONST_INT SANDSTORM_DESERT       19


pegar_celular:
    //celular fora da mão
    READ_MEMORY ponteiro 4 FALSE var
    IF var = este_app
        WRITE_MEMORY ponteiro 4 -1 FALSE
        GOTO pegar_celular
    ENDIF
    IF var = -1
        USE_TEXT_COMMANDS 1
        GOSUB previsão
        USE_TEXT_COMMANDS 0
    ENDIF
    WAIT 0
GOTO pegar_celular

previsão:

    GET_TIME_OF_DAY hour min
    READ_MEMORY 0xC81320 2 FALSE (curWeather)
    READ_MEMORY 0xC8131C 2 FALSE (nextWeather)

    SWITCH curWeather

        CASE EXTRASUNNY_LA
        CASE EXTRASUNNY_SMOG_LA
        CASE EXTRASUNNY_SF
        CASE EXTRASUNNY_VEGAS
        CASE EXTRASUNNY_COUNTRYSIDE
        CASE EXTRASUNNY_DESERT
            SWITCH nextWeather

                CASE CLOUDY_LA
                CASE CLOUDY_SF
                CASE CLOUDY_VEGAS
                CASE CLOUDY_COUNTRYSIDE
                    sprite = SPRITE_SUNNY_S_CLOUDY
                    BREAK

                CASE RAINY_SF
                CASE RAINY_COUNTRYSIDE
                    sprite = SPRITE_SUNNY_S_RAIN
                    BREAK

                DEFAULT 
                    sprite = SPRITE_SUNNY
                    BREAK

            ENDSWITCH
            BREAK

        CASE SUNNY_LA
        CASE SUNNY_SMOG_LA
        CASE SUNNY_SF
        CASE SUNNY_VEGAS
        CASE SUNNY_COUNTRYSIDE
        CASE SUNNY_DESERT
            SWITCH nextWeather

                CASE CLOUDY_LA
                CASE CLOUDY_SF
                CASE CLOUDY_VEGAS
                CASE CLOUDY_COUNTRYSIDE
                    sprite = SPRITE_SUNNY_S_CLOUDY
                    BREAK

                CASE RAINY_SF
                CASE RAINY_COUNTRYSIDE
                    sprite = SPRITE_SUNNY_S_RAIN
                    BREAK

                DEFAULT
                    sprite = SPRITE_PARTLY_CLOUD
                    BREAK

            ENDSWITCH
            BREAK
            
        CASE CLOUDY_LA
        CASE CLOUDY_SF
        CASE CLOUDY_VEGAS
        CASE CLOUDY_COUNTRYSIDE
            SWITCH nextWeather

                CASE SUNNY_LA
                CASE SUNNY_SMOG_LA
                CASE SUNNY_SF
                CASE SUNNY_VEGAS
                CASE SUNNY_COUNTRYSIDE
                CASE SUNNY_DESERT
                CASE EXTRASUNNY_LA
                CASE EXTRASUNNY_SMOG_LA
                CASE EXTRASUNNY_SF
                CASE EXTRASUNNY_VEGAS
                CASE EXTRASUNNY_COUNTRYSIDE
                CASE EXTRASUNNY_DESERT
                    sprite = SPRITE_CLOUDY_S_SUNNY
                    BREAK

                CASE RAINY_SF
                CASE RAINY_COUNTRYSIDE
                    sprite = SPRITE_CLOUDY_S_RAIN
                    BREAK

                DEFAULT
                    sprite = SPRITE_CLOUDY
                    BREAK

            ENDSWITCH
            BREAK

        CASE RAINY_SF
        CASE RAINY_COUNTRYSIDE
            SWITCH nextWeather

                CASE SUNNY_LA
                CASE SUNNY_SMOG_LA
                CASE SUNNY_SF
                CASE SUNNY_VEGAS
                CASE SUNNY_COUNTRYSIDE
                CASE SUNNY_DESERT
                CASE EXTRASUNNY_LA
                CASE EXTRASUNNY_SMOG_LA
                CASE EXTRASUNNY_SF
                CASE EXTRASUNNY_VEGAS
                CASE EXTRASUNNY_COUNTRYSIDE
                CASE EXTRASUNNY_DESERT
                    sprite = SPRITE_RAIN_S_SUNNY
                    BREAK

                CASE CLOUDY_LA
                CASE CLOUDY_SF
                CASE CLOUDY_VEGAS
                CASE CLOUDY_COUNTRYSIDE
                    sprite = SPRITE_RAIN_S_CLOUDY
                    BREAK

                DEFAULT
                    sprite = SPRITE_THUNDERSTORMS
                    BREAK

            ENDSWITCH
            BREAK

        CASE FOGGY_SF
            sprite = SPRITE_FOG
            BREAK

        CASE SANDSTORM_DESERT
            sprite = SPRITE_WINDY
            BREAK
        
        DEFAULT
            sprite = SPRITE_SUNNY
            BREAK

    ENDSWITCH

    READ_MEMORY 0x713D2C 1 FALSE (night_start_hour) // stars render (compatible with Change Night Hour mod)

    // night
    IF hour >= night_start_hour
    OR hour <= 5
        
        SWITCH sprite

            CASE SPRITE_CLOUDY_S_SUNNY
            CASE SPRITE_SUNNY_S_CLOUDY
            CASE SPRITE_PARTLY_CLOUD
                sprite = SPRITE_NIGHT_PARTLY_CLOUDY
                BREAK

            CASE SPRITE_SUNNY
                sprite = SPRITE_NIGHT
                BREAK

            CASE SPRITE_RAIN
            CASE SPRITE_RAIN_S_SUNNY
            CASE SPRITE_RAIN_S_CLOUDY
            CASE SPRITE_SUNNY_S_RAIN
                sprite = SPRITE_RAIN_NIGHT
                BREAK

        ENDSWITCH

    ENDIF

    posX = pos_x + 1.0
    posY = pos_y + 1.0
    DRAW_SPRITE sprite (posX posY) (sizX sizY) (0 0 0 180)

    DRAW_SPRITE sprite (pos_x pos_y) (tam_x tam_y) (255 255 255 255)

RETURN
}
SCRIPT_END