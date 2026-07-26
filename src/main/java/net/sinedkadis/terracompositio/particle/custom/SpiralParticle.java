package net.sinedkadis.terracompositio.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SpiralParticle extends TextureSheetParticle {

    /**
     * Скорость, с которой "длина вектора" (тики) конвертируется в реальное расстояние.
     */
    public static final double AXIAL_SPEED = 0.01;
    public static final int MAX_LIFETIME = 20 * 60;
    private static final double PHI = (1.0 + Math.sqrt(5.0)) * 0.5;
    /**
     * Минимальный радиус — не даём спирали вырождаться в точку и ловить деление на 0.
     */
    private static final double MIN_RADIUS = 0.05;

    /**
     * Гарантированный минимум оборотов, даже если дистанция маленькая.
     */
    private static final double MIN_ANGLE_TOTAL = Math.PI * 2; // 1 полный оборот

    /**
     * Скорость роста радиуса лог-спирали: рост в φ раз за каждую четверть оборота.
     */
    private static final double GOLDEN_RATE = Math.log(PHI) / (Math.PI / 2.0);

    private final double startX, startY, startZ;
    private final double endX, endY, endZ;

    private final double centerX, centerZ; // вертикальная ось вращения (origin по XZ)
    private final double radiusStart, radiusEnd;
    private final double angle0;        // начальный угол (чтобы не было скачка позиции)
    private final double angleTotal;    // полный угол поворота за всю жизнь партикла
    private final double rotationSign;  // +1 / -1 — направление закрутки

    protected SpiralParticle(ClientLevel level, double x, double y, double z,
                             double xSpeed, double ySpeed, double zSpeed,
                             SpriteSet spriteSet, boolean originAtStart, double rotationSign) {
        super(level, x, y, z);

        this.startX = x;
        this.startY = y;
        this.startZ = z;
        this.rotationSign = rotationSign;

        double length = Math.sqrt(xSpeed * xSpeed + ySpeed * ySpeed + zSpeed * zSpeed);

        if (length < 1.0e-6) {
            this.lifetime = 20;
            this.endX = x;
            this.endY = y;
            this.endZ = z;
        } else {
            this.lifetime = Mth.clamp((int) Math.round(length), 1, MAX_LIFETIME);
            // конец пути восстанавливается прямо из вектора: направление*лайфтайм*AXIAL_SPEED = точное расстояние до цели
            this.endX = x + xSpeed * AXIAL_SPEED;
            this.endY = y + ySpeed * AXIAL_SPEED;
            this.endZ = z + zSpeed * AXIAL_SPEED;
        }

        // ось вращения (по XZ) — либо точка спавна (прямая спираль), либо точка финиша (обратная спираль)
        if (originAtStart) {
            this.centerX = this.startX;
            this.centerZ = this.startZ;
        } else {
            this.centerX = this.endX;
            this.centerZ = this.endZ;
        }

        double rStartRaw = Math.hypot(this.startX - this.centerX, this.startZ - this.centerZ);
        double rEndRaw = Math.hypot(this.endX - this.centerX, this.endZ - this.centerZ);

        this.radiusStart = Math.max(rStartRaw, MIN_RADIUS);
        this.radiusEnd = Math.max(rEndRaw, MIN_RADIUS);

        // начальный угол — направление от центра к точке спавна, чтобы не было визуального скачка
        this.angle0 = Math.atan2(this.startZ - this.centerZ, this.startX - this.centerX);

        // угол, за который лог-спираль с "золотой" скоростью роста меняет радиус от radiusStart до radiusEnd
        double rawAngle = Math.abs(Math.log(this.radiusEnd / this.radiusStart)) / GOLDEN_RATE;
        this.angleTotal = Math.max(rawAngle, MIN_ANGLE_TOTAL);

        this.xd = 0.0;
        this.yd = 0.0;
        this.zd = 0.0;
        this.hasPhysics = false;
        this.gravity = 0.0f;

        this.quadSize = 0.08f;
        this.pickSprite(spriteSet);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        double s = (double) this.age / (double) this.lifetime; // 0..1, прогресс жизни

        // радиус меняется ЛИНЕЙНО по времени — равномерное движение, без "зависания в начале / рывка в конце"
        double radius = Mth.lerp(s, this.radiusStart, this.radiusEnd);

        // угол закрутки — линейный по времени, общий угол (angleTotal) выведен из "золотой" зависимости радиус/угол
        double angle = this.angle0 + this.rotationSign * this.angleTotal * s;

        double cosA = Math.cos(angle);
        double sinA = Math.sin(angle);

        this.x = this.centerX + radius * cosA;
        this.z = this.centerZ + radius * sinA;
        this.y = Mth.lerp(s, this.startY, this.endY);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    /**
     * Обычная спираль — крутится по часовой стрелке относительно оси движения.
     */
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new SpiralParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet, true, 1.0);
        }
    }

    /**
     * Зеркальная спираль — крутится в противоположную сторону.
     */
    public static class ReverseProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public ReverseProvider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new SpiralParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet, false, -1.0);
        }
    }
}
