package net.sinedkadis.terracompositio.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SpiralParticle extends TextureSheetParticle {

    public static final int MAX_LIFETIME = 20 * 60;

    private final double startY;
    private final double endY;
    private final double centerX, centerZ; // вертикальная ось вращения (origin по XZ)
    private final double radiusStart, radiusEnd;
    private final double angle0;        // начальный угол (чтобы не было скачка позиции)
    private final double angleTotal;    // полный угол поворота за всю жизнь партикла
    private final double rotationSign;  // +1 / -1 — направление закрутки
    protected SpiralParticle(ClientLevel level, double x, double y, double z,
                             double xSpeed, double ySpeed, double zSpeed,
                             SpriteSet spriteSet, boolean originAtStart, double rotationSign) {
        super(level, x, y, z);

        this.startY = y;
        this.rotationSign = rotationSign;

        double length = Math.sqrt(xSpeed * xSpeed + ySpeed * ySpeed + zSpeed * zSpeed);

        double endX;
        double endZ;
        if (length < 1.0e-6) {
            this.lifetime = 20;
            endX = x;
            this.endY = y;
            endZ = z;
        } else {
            this.lifetime = Mth.clamp((int) (Math.round(length) / 4), 1, MAX_LIFETIME);
            endX = x + xSpeed * getAxialSpeed();
            this.endY = y + ySpeed * getAxialSpeed();
            endZ = z + zSpeed * getAxialSpeed();
        }

        if (originAtStart) {
            this.centerX = x;
            this.centerZ = z;
        } else {
            this.centerX = endX;
            this.centerZ = endZ;
        }

        double rStartRaw = Math.hypot(x - this.centerX, z - this.centerZ);
        double rEndRaw = Math.hypot(endX - this.centerX, endZ - this.centerZ);

        this.radiusStart = Math.max(rStartRaw, getMinRadius());
        this.radiusEnd = Math.max(rEndRaw, getMinRadius());

        double rawAngle = Math.abs(Math.log(this.radiusEnd / this.radiusStart)) / getRate();
        this.angleTotal = Math.max(rawAngle, getMinAngleTotal());

        if (originAtStart) {
            double farAngle = Math.atan2(endZ - this.centerZ, endX - this.centerX);
            this.angle0 = farAngle - this.rotationSign * this.angleTotal;
        } else {
            // дальняя точка — старт пути (target), как и раньше — тут всё было корректно.
            this.angle0 = Math.atan2(z - this.centerZ, x - this.centerX);
        }

        this.xd = 0.0;
        this.yd = 0.0;
        this.zd = 0.0;
        this.hasPhysics = false;
        this.gravity = 0.0f;

        this.quadSize = 0.08f;
        this.pickSprite(spriteSet);
        this.scale(1.5f);
    }

    public static double getAxialSpeed() {
        return 0.01;
    }

    public static double getMinRadius() {
        return 0.000000001d;
    }

    public static double getMinAngleTotal() {
        return 0;
    }

    private static double getRate() {
        return 5;
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

        double lerp = Mth.lerp((double) age / lifetime, 0.18d, 0.01d);
        this.quadSize = (float) lerp;
        //this.scale((float) lerp);
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
