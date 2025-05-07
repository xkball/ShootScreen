#version 150

#define SimScreenType rgbw
uniform sampler2D DiffuseSampler;
uniform sampler2D Background;
uniform vec2 OutSize;
uniform mat3 Homography;
uniform mat4 LightData;

in vec2 texCoord;
out vec4 fragColor;

const vec3 gamma_ = vec3(1.1,1.2,1.1);


const mat2 rgbw = mat2(
        1,3,
        2,0
);

const mat2 bayer = mat2(
        1,0,
        2,1
);

const mat4 mask = mat4(
        1,0,0,0,
        0,1,0,0,
        0,0,1,0,
        0,0,0,1
);

vec3 getByChannel(int index, vec4 color) {
    float light = dot(color.rgb, vec3(0.299, 0.587, 0.114));
    vec4 temp = vec4(color.rgb,light)*mask[index];
    return (temp.rgb + vec3(temp.a))*1;
}

vec3 gamma(vec3 color) {
    return pow(color, 1.0 / gamma_);
}

vec4 applyPhongLight(vec4 color,vec2 uv){
    vec3 viewPos = LightData[0].xyz;
    vec3 lightPos = LightData[1].xyz;
    vec3 lightColor = LightData[2].xyz;
    float ambientStrength = LightData[3][1];
    float specularStrength = LightData[3][2];
    float shininess = LightData[3][3];

    vec3 ambient = ambientStrength * lightColor;

    const vec3 norm = vec3(0,0,1);
    vec3 fragPos = vec3(uv.xy,0);
    vec3 lightDir = normalize(lightPos - fragPos);
    vec3 diffuse = max(dot(norm, lightDir), 0.0) * lightColor;

//    vec3 viewPos = vec3(0.6,0.5,1);
//    const float specularStrength = 10;
    vec3 viewDir = normalize(viewPos - fragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), shininess);
    vec3 specular = specularStrength * spec * lightColor;
    return vec4((ambient + diffuse + specular) * color.rgb,color.a);
}

void main() {
    vec3 remappedCoord = Homography * vec3(texCoord, 1.0);
    vec2 uv = remappedCoord.xy / remappedCoord.z;
    if (uv.x < 0.0 || uv.x > 1.0 || uv.y < 0.0 || uv.y > 1.0) {
        fragColor = texture(Background,vec2(texCoord.x,1-texCoord.y));
        return;
    }
    vec4 color = texture(DiffuseSampler, uv);
    vec3 c = getByChannel(int(SimScreenType[int(uv.y*OutSize.y)%2][int(uv.x*OutSize.x)%2]),color);
    float moorishStrength = LightData[3][0];
    fragColor = applyPhongLight(vec4(gamma(c)*moorishStrength+color.rgb*(1-moorishStrength), color.a),uv);
}