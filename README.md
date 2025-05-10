
Shoot Screen
=======

Make your game screen look like a screenshot.

Press 'Z' to toggle of screenshot like rendering.

Supprt custom background image and many other params.

## Custom Background

Put following files in a resource pack.

```
├───assets
│   └───<namespace>
│       └───custom_background
│  			└─── <image_name>.json //background config
│  		└───texutres
│  			└───custom_background
│  				└─── <image_name>.png
```

If there is more than one background, will apply a random one.

```<image_name.json>
//A background config example.
{
  "name": "example_background", //the image name.
    "screenRect": {				//define the sreen corners in background.
        "leftDown": [			//Percentage[0-1] form left down corner of iamge.
          0.106,
          0.249
        ],
        "leftUp": [
          0.0879,
          0.876
        ],
        "rightDown": [
          0.952,
          0.256
        ],
        "rightUp": [
          0.951,
          0.92
        ]
  },
  "moireStrength": 0.6,	//Strength of Moiré pattern.Range[0,1]. 0 means no Moiré pattern.
  "lightColor": [		//Following are the light parameters
    1.0,
    1.0,
    1.0
  ],
  "lightPos": [
    1.7,
    1.0,
    2.0
  ],
    "viewPos": [
    0.6,
    0.5,
    1.0
  ]
  "ambientStrength": 0.2,
  "specularStrength": 5,
  "shininess": 16,
}
```

About light parameters: this mod use Phong Lighting Model to simulate highlighting effects. The screen mapped to a rect from `(0,0,0)` to` (1,1,0) `and towards`(0,0,1)`.

**If you just want to disable highlighing effects , set specular strength to 0.**
