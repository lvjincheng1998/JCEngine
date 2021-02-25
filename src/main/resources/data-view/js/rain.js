let canvas = document.querySelector("canvas");
let ctx = canvas.getContext("2d");
function resizeCanvas() {
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
};
resizeCanvas();
window.addEventListener("resize", resizeCanvas);

function randomByRange(min, max) {
    return Math.random() * (max - min) + min
}
function Rain() {} //生成雨滴的封装函数
Rain.prototype = {
    init: function() {
        this.x = randomByRange(0, canvas.width); //雨滴初始的x坐标
        this.y = 0; //雨滴初始的y坐标
        this.v = randomByRange(4, 5); //下落加速度
        this.h = randomByRange(0.8 * canvas.height, 0.9 * canvas.height); //雨滴下落的地面
        this.r = 1; //初始半径
        this.vr = randomByRange(0.4, 0.6); //半径增长率
        this.a = 1; //初始透明度
        this.va = 0.96; // 透明度变化系数
    },
    draw: function() {
        if (this.y < this.h) {
            ctx.fillStyle = "#33ffff"; //拿一只画实心图形的红色的笔
            ctx.fillRect(this.x, this.y, 2, 10); // 画一个实心的矩形
        } else {
            ctx.strokeStyle = "rgba(51,255,255," + this.a + ")";
            ctx.beginPath(); //重新拿起笔
            ctx.ellipse(this.x, this.y, this.r, this.r * 0.33, 0, 0, Math.PI * 2);
            ctx.stroke();
        }
    },
    move: function() {
        if (this.y < this.h) {
            this.y += this.v;
        } else {
            if (this.a > 0.02) {
                this.r += this.vr;
                if (this.r > 50) {
                    this.a *= this.va
                }
            } else {
                this.init();
            }
        }
        this.draw();
    }
}

let rainArray = [];
function createRain() {
    let rain = new Rain();
    rain.init();
    rain.draw();
    rainArray.push(rain);
}
for (let i = 0; i < 30; i++) {
    setTimeout(createRain, 200 * i);
}

function moveRain() {
    ctx.fillStyle = "rgba(0, 0, 0, 0.05)";
    ctx.fillRect(0, 0, canvas.width, canvas.height);
    for (let k = 0; k < rainArray.length; k++) {
        rainArray[k].move();
    }
}
!function run() {
    moveRain();
    setTimeout(run, 100 / 60);
}();