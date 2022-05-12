import acoustics.bands as band
import acoustics.room as room
import numpy as np


def rt60(wav):
    octave_bands = band.octave(100, 5000)
    t60 = room.t60_impulse(wav, octave_bands, 't20')
    np.set_printoptions(formatter={'float_kind': lambda x: "{0:0.2f}".format(x)})

    return np.round(t60, 2)

